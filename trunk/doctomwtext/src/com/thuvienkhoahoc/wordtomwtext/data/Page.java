package com.thuvienkhoahoc.wordtomwtext.data;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.jwbf.contentRep.mw.SimpleArticle;

public class Page extends SimpleArticle {
	
	private Page parent = null;
	private List<Page> children = new ArrayList<Page>();
	private boolean markedForRemoval;
	
	public Page(Page parent) {
		super();
		this.parent = parent;
	}
	
	public Page getParent() {
		return parent;
	}
	
	public void setParent(Page parent) {
		this.parent = parent;
	}
	
	public List<Page> getChildren() {
		return children;
	}

	public boolean isSubpage() {
		return parent != null;
	}
	
	public boolean hasChild() {
		return children.size() > 0;
	}
	
	public String getShortLabel() {
		if (isSubpage()) {
			return getLabel().substring(parent.getLabel().length());
		} 
		return getLabel();
	}
	
	public void setShortLabel(String label) {
		if (isSubpage()) {
			setLabel(parent.getLabel() + "/" + label);
		} else {
			setLabel(label);
		}
	}
	
	@Override
	public void setLabel(String label) {
		if (isSubpage() && !label.startsWith(parent.getLabel() + "/")) {
			throw new IllegalArgumentException("Subpage label must start with parent label plus '/'. Try to use setShortLabel() instead.");
		}
		super.setLabel(label);
	}

	public void setMarkedForRemoval(boolean markedForRemoval) {
		this.markedForRemoval = markedForRemoval;
	}

	public boolean isMarkedForRemoval() {
		return markedForRemoval;
	}
	
}
