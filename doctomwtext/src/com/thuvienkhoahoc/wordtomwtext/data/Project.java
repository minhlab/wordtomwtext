package com.thuvienkhoahoc.wordtomwtext.data;

import java.util.ArrayList;
import java.util.List;

public class Project {

	List<Page> pages = new ArrayList<Page>();
	List<Image> images = new ArrayList<Image>();

	public List<Image> getImages() {
		return images;
	}
	
	public List<Page> getPages() {
		return pages;
	}
	
	public void importData(Project anotherProject) {
		pages.addAll(anotherProject.pages);
		images.addAll(anotherProject.getImages());
	}
	
}
