package info.cloud;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

import javax.inject.Named;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.response.NotFoundException;
import com.google.api.services.calendar.model.Event;

import info.cloud.services.GoogleCalendar;
import info.cloud.services.GoogleMemcache;
import info.cloud.services.GoogleSearch;
import info.cloud.util.GoogleResult;

@Api(name = "cloudapi", version = "v1", scopes = { Constants.EMAIL_SCOPE }, clientIds = { Constants.WEB_CLIENT_ID,
		Constants.ANDROID_CLIENT_ID, Constants.IOS_CLIENT_ID,
		Constants.API_EXPLORER_CLIENT_ID }, audiences = { Constants.ANDROID_AUDIENCE })
public class GoogleServices {

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
			}
			return result;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Collections.emptyList();
	}
}
