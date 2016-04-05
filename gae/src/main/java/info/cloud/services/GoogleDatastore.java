package info.cloud.services;

import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

import info.cloud.util.QueryDetails;

public class GoogleDatastore {

	private static final int MAX_COUNT = 20;

	public List<QueryDetails> queryDatastore() {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		PreparedQuery preparedQuery = datastore
				.prepare(new Query("Query").addSort("requestTime", Query.SortDirection.DESCENDING));
		
		return parseResults(preparedQuery);
	}

	public void saveInDatastore(String query, Boolean fromCache, long executionDuration) {
		Entity messageToStore = new Entity("Query");
		messageToStore.setProperty("query", query);
		messageToStore.setProperty("fromCache", fromCache);
		messageToStore.setProperty("duration", executionDuration);
		messageToStore.setProperty("requestTime", System.currentTimeMillis());

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		datastore.put(messageToStore);
	}
	
	private List<QueryDetails> parseResults(PreparedQuery query) {
		List<QueryDetails> queries = new ArrayList<>();

		String executedQuery;
		Boolean fromCache;
		long duration;
		long requestTime;
		for (Entity entity : query.asIterable(FetchOptions.Builder.withLimit(MAX_COUNT))) {
			executedQuery = (String) entity.getProperty("query");
			fromCache = (Boolean) entity.getProperty("fromCache");
			duration = (long) entity.getProperty("duration");
			requestTime = (long) entity.getProperty("requestTime");

			queries.add(new QueryDetails(executedQuery, fromCache, duration, requestTime));
		}

		return queries;
	}
}
