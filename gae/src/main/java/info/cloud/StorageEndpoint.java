package info.cloud;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.appengine.tools.cloudstorage.*;
import com.google.appengine.api.blobstore.*;

import javax.inject.Named;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by claudiu.iordache on 4/3/16.
 */

@Api(
        name = "storage",
        version = "v1",
        scopes = {Constants.EMAIL_SCOPE},
        clientIds = {Constants.WEB_CLIENT_ID, Constants.ANDROID_CLIENT_ID, Constants.IOS_CLIENT_ID, Constants.API_EXPLORER_CLIENT_ID},
        audiences = {Constants.ANDROID_AUDIENCE}
)
public class StorageEndpoint {


    public static final boolean SERVER_USING_BLOBSTORE_API = true;

    /**
     * This is where backoff parameters are configured. Here it is aggressively retrying with
     * backoff, up to 10 times but taking no more that 15 seconds total to do so.
     */
    private final GcsService gcsService = GcsServiceFactory.createGcsService(new RetryParams.Builder()
            .initialRetryDelayMillis(10)
            .retryMaxAttempts(10)
            .totalRetryPeriodMillis(15000)
            .build());

    /**Used below to determine the size of chucks to read in. Should be > 1kb and < 10MB */
    private static final int BUFFER_SIZE = 2 * 1024 * 1024;

    @ApiMethod(name = "storage.upload", path = "gcs/{bucketId}/{fileId}", httpMethod = ApiMethod.HttpMethod.POST)
    public void upload(HttpServletRequest req,@Named("bucketId") String buckedId, @Named("fileId") String fileId) throws IOException {
        GcsFilename name = new GcsFilename(buckedId, fileId);
        GcsOutputChannel outputChannel =
                gcsService.createOrReplace(name, GcsFileOptions.getDefaultInstance());
        copy(req.getInputStream(), Channels.newOutputStream(outputChannel));
    }

    @ApiMethod(name = "storage.retrieve", path = "gcs/{bucketId}/{fileId}", httpMethod = ApiMethod.HttpMethod.GET)
    public void retrieve(HttpServletResponse resp, @Named("bucketId") String buckedId, @Named("fileId") String fileId) throws IOException {
        GcsFilename fileName =  new GcsFilename(buckedId, fileId);

        if (SERVER_USING_BLOBSTORE_API) {
            BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
            BlobKey blobKey = blobstoreService.createGsBlobKey(
                    "/gcs/" + fileName.getBucketName() + "/" + fileName.getObjectName());
            blobstoreService.serve(blobKey, resp);
        } else {
            GcsInputChannel readChannel = gcsService.openPrefetchingReadChannel(fileName, 0, BUFFER_SIZE);
            copy(Channels.newInputStream(readChannel), resp.getOutputStream());
        }
    }

    /**
     * Transfer the data from the inputStream to the outputStream. Then close both streams.
     */
    private void copy(InputStream input, OutputStream output) throws IOException {
        try {
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead = input.read(buffer);
            while (bytesRead != -1) {
                output.write(buffer, 0, bytesRead);
                bytesRead = input.read(buffer);
            }
        } finally {
            try {
                input.close();
                output.close();
            } catch (IOException ex) {
            }
        }
    }

}
