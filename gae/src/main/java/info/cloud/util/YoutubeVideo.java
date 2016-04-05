package info.cloud.util;

public class YoutubeVideo {

	private String title;
	
	private String thumbnailUrl;
	
	private String url;
	
	public YoutubeVideo(){
		
	}
	
	public YoutubeVideo(String title, String url, String thumbnailUrl) {
		this.title = title;
		this.url = url;
		this.thumbnailUrl = thumbnailUrl;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
