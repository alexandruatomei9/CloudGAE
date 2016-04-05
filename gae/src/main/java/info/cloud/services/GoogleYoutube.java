package info.cloud.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Thumbnail;

import info.cloud.util.YoutubeVideo;

public class GoogleYoutube {

	String apiKey = "AIzaSyDM0hfA7esxhrbm5dIlZ3fADd4eybEJGIw";
	
	private static final long NUMBER_OF_VIDEOS_RETURNED = 5;
    private static YouTube youtube;
	
	public List<YoutubeVideo> search(String queryTerm) {

		List<YoutubeVideo> results = new ArrayList<>();
		
        try {
            youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
                public void initialize(HttpRequest request) throws IOException {
                }
            }).setApplicationName("CloudApp").build();
            
            YouTube.Search.List search = youtube.search().list("id,snippet");

            search.setKey(apiKey);
            search.setQ(queryTerm);
            search.setType("video");
            search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");
            search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);

            SearchListResponse searchResponse = search.execute();
            List<SearchResult> searchResultList = searchResponse.getItems();
            
            if (searchResultList != null) {
            	Iterator<SearchResult> iteratorSearchResults = searchResultList.iterator();
                while (iteratorSearchResults.hasNext()) {
                    SearchResult singleVideo = iteratorSearchResults.next();
                    ResourceId rId = singleVideo.getId();
                    if (rId.getKind().equals("youtube#video")) {
                        Thumbnail thumbnail = singleVideo.getSnippet().getThumbnails().getDefault();
                        YoutubeVideo video = new YoutubeVideo();
                    	video.setTitle(singleVideo.getSnippet().getTitle());
                    	video.setUrl("https://www.youtube.com/watch?v=" + rId.getVideoId());
                    	video.setThumbnailUrl(thumbnail.getUrl());
                    	results.add(video);
                    }
                }
            }
        } catch (GoogleJsonResponseException e) {
           e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } 
        
        return results;
	}
}
