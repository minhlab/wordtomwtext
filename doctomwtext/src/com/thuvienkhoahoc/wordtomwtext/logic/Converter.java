package com.thuvienkhoahoc.wordtomwtext.logic;

import java.io.File;

import com.thuvienkhoahoc.wordtomwtext.data.Page;
import com.thuvienkhoahoc.wordtomwtext.data.Project;

public class Converter {

	private static int identity = 0;
	
	public Project convert(File wordFile) {
		Page page = new Page(null);
		page.setLabel("test" + (++identity));
		page.setText("test");
		
		Project project = new Project();
		project.getPages().add(page);
		
		return project;
	}
	
}
