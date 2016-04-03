package info.cloud;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.inject.Named;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.response.NotFoundException;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.storage.model.StorageObject;
import com.google.appengine.api.users.User;

import info.cloud.services.GoogleCalendar;
import info.cloud.services.GoogleMemcache;
import info.cloud.services.GoogleSearch;
import info.cloud.services.storage.StorageSample;
import info.cloud.util.GoogleResult;

@Api(name = "cloudapi", version = "v1", scopes = { Constants.EMAIL_SCOPE }, clientIds = { Constants.WEB_CLIENT_ID,
		Constants.ANDROID_CLIENT_ID, Constants.IOS_CLIENT_ID,
		Constants.API_EXPLORER_CLIENT_ID }, audiences = { Constants.ANDROID_AUDIENCE })
public class GoogleServices {

	@ApiMethod(name = "googleServices.multiply", httpMethod = "post")
	public HelloGreeting insertGreeting(@Named("times") Integer times, HelloGreeting greeting) {
		HelloGreeting response = new HelloGreeting();
		StringBuilder responseBuilder = new StringBuilder();
		for (int i = 0; i < times; i++) {
			responseBuilder.append(greeting.getMessage());
		}
		response.setMessage(responseBuilder.toString());
		return response;
	}

	@ApiMethod(name = "googleServices.authed", path = "hellogreeting/authed")
	public HelloGreeting authedGreeting(User user) {
		HelloGreeting response = new HelloGreeting("hello " + user.getEmail());
		return response;
	}

	public List<Event> getCalendar() {
		GoogleCalendar calendar = new GoogleCalendar();
		List<Event> events = Collections.emptyList();
		try {
			events = calendar.getEvents(20);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return events;
	}

	public List<GoogleResult> search(@Named("query") String query) throws NotFoundException, GeneralSecurityException {
		GoogleSearch googleSearch = new GoogleSearch();
		try {
			List<GoogleResult> result = GoogleMemcache.getInstance().findInCache(query);
			if (result == null) {
				result = googleSearch.search(query);
				GoogleMemcache.getInstance().putInCache(query, result);
				updateLatestSearchesFile(query);
			}
			return result;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Collections.emptyList();
	}

	public Set<String> getCachedSearches() {

		return null;
	}

	private void updateLatestSearchesFile(String query) throws IOException, GeneralSecurityException {
		JSONObject existingSearches = readJsonFile("cloud-app-storage", "searches.json");
		JSONObject newContent = addNewRecord(existingSearches, query);
		File temp = File.createTempFile("LatestSearches", ".json");
		BufferedWriter bw = new BufferedWriter(new FileWriter(temp));
	    bw.write(newContent.toString());
	    bw.close();
		StorageSample.uploadFile("searches.json", "application/json", temp, "cloud-app-storage");
	}

	private JSONObject readJsonFile(String bucketName, String fileName) throws IOException, GeneralSecurityException {
		List<StorageObject> bucketContents = StorageSample.listBucket(bucketName);
		StorageObject myFile = null;
		if (null != bucketContents) {
			for (StorageObject object : bucketContents) {
				if (object.getName().equals(fileName)) {
					myFile = object;
				}
			}
		}

		if (myFile != null) {
			JSONParser parser = new JSONParser();
			try {
				Object obj = parser.parse(myFile.toPrettyString());

				return (JSONObject) obj;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	private JSONObject addNewRecord(JSONObject jsonObject, String query) {
		if (jsonObject != null) {
			JSONArray searches = (JSONArray) jsonObject.get("searches");
			JSONObject newRecord = new JSONObject();
			newRecord.put("query", query);
			searches.add(newRecord);

			return jsonObject;
		} else {
			JSONObject newJsonObject = new JSONObject();
			JSONArray searches = new JSONArray();
			JSONObject newRecord = new JSONObject();
			newRecord.put("query", query);
			searches.add(newRecord);
			newJsonObject.put("searches", searches);
			return newJsonObject;
		}
	}
}
