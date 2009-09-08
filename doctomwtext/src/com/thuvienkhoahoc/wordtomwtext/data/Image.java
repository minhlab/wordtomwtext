package com.thuvienkhoahoc.wordtomwtext.data;

import java.io.File;

import net.sourceforge.jwbf.contentRep.mw.SimpleFile;

public class Image extends SimpleFile {

	private boolean markedForRemoval;
	
	public Image(String Label, File Filename) {
		super(Label, Filename);
	}

	public void setMarkedForRemoval(boolean markedForRemoval) {
		this.markedForRemoval = markedForRemoval;
	}

	public boolean isMarkedForRemoval() {
		return markedForRemoval;
	}

}
