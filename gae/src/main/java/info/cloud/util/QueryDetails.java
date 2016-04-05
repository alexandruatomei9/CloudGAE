package info.cloud.util;

public class QueryDetails {

	private String query;
	
	private Boolean fromCache;
	
	private long duration;
	
	private long requestTime;
	
	public QueryDetails(){
		
	}
	
	public QueryDetails(String query, Boolean fromCache, long duration, long requestTime) {
		this.query = query;
		this.fromCache = fromCache;
		this.duration = duration;
		this.requestTime = requestTime;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public Boolean getFromCache() {
		return fromCache;
	}

	public void setFromCache(Boolean fromCache) {
		this.fromCache = fromCache;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public long getRequestTime() {
		return requestTime;
	}

	public void setRequestTime(long requestTime) {
		this.requestTime = requestTime;
	}
}
