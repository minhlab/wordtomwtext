package com.thuvienkhoahoc.wordtomwtext.data;

@SuppressWarnings("serial")
public class DuplicatedTitleException extends RuntimeException {

	private String title;

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	public DuplicatedTitleException(String title) {
		super();
		this.title = title;
	}
	
}
