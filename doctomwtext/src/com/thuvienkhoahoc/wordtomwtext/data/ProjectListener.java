package com.thuvienkhoahoc.wordtomwtext.data;

import java.util.EventListener;

public interface ProjectListener extends EventListener {

	void pageAdded(ProjectEvent evt);
	
	void imageAdded(ProjectEvent evt);
	
	void pageRemoved(ProjectEvent evt);
	
	void imageRemoved(ProjectEvent evt);

	void pagePropertyChanged(ProjectEvent evt);
	
	void imagePropertyChanged(ProjectEvent evt);
	
	void projectChanged(ProjectEvent evt);
	
}
