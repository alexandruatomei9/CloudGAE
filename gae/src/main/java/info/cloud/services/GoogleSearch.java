package info.cloud.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.customsearch.Customsearch;
import com.google.api.services.customsearch.model.Result;
import com.google.api.services.customsearch.model.Search;

import info.cloud.util.GoogleResult;

public class GoogleSearch {

	private String CX = "016033317711452572654:nomekxlbzmm";
	private String API_KEY = "AIzaSyDM0hfA7esxhrbm5dIlZ3fADd4eybEJGIw";

	public List<GoogleResult> search(String query) throws IOException {
		Customsearch customsearch = new Customsearch(new NetHttpTransport(), new JacksonFactory(), null);
		com.google.api.services.customsearch.Customsearch.Cse.List list = customsearch.cse().list(query);
		list.setKey(API_KEY);
		list.setCx(CX);
		Search searchResult = list.execute();
		List<Result> items = searchResult.getItems();

		List<GoogleResult> results = new ArrayList<GoogleResult>();
		for (Result item : items) {
			results.add(new GoogleResult(item.getLink(), item.getTitle()));
		}

		return results;
	}
}
