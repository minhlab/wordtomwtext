package com.thuvienkhoahoc.wordtomwtext;

public class Page {

	String title;
	String text;
	
	public Page(String title, String text) {
		super();
		this.title = title;
		this.text = text;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	public boolean isSubpage() {
		return title.contains("/");
	}
	
}
