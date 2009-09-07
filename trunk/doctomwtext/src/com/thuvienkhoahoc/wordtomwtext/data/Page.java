package com.thuvienkhoahoc.wordtomwtext.data;

import net.sourceforge.jwbf.contentRep.mw.SimpleArticle;

public class Page extends SimpleArticle {
	
	public boolean isSubpage() {
		return getLabel().contains("/");
	}
	
}
