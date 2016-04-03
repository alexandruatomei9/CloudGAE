package info.cloud.util;

import java.io.Serializable;

public class GoogleResult implements Serializable{

	private static final long serialVersionUID = 1L;
	private String url;
	private String title;
	
	public GoogleResult(){}
	
	public GoogleResult(String url, String title){
		this.url = url;
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public String getTitle() {
		return title;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String toString() {
		return "Result[url:" + url + ",title:" + title + "]";
	}
}
