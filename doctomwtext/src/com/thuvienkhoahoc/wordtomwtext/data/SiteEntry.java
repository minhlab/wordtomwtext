package com.thuvienkhoahoc.wordtomwtext.data;

public class SiteEntry {

	public static SiteEntry[] DEFAULT_SITES = {
		new SiteEntry("Tủ sách khoa học", "http://thuvienkhoahoc.com/tusach/"),
		new SiteEntry("Thư viện đề thi", "http://thuvienkhoahoc.com/dethi/"),
		new SiteEntry("Khoa học huyền bí", "http://thuvienkhoahoc.com/tuvi/"),
	};
	
	private String name;
	private String url;
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}
	
	@Override
	public String toString() {
		return name + " - " + url;
	}
	
	public SiteEntry(String name, String url) {
		super();
		this.name = name;
		this.url = url;
	}
	
}
