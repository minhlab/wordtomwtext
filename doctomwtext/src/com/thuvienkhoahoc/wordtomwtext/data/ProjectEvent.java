package com.thuvienkhoahoc.wordtomwtext.data;

import java.util.EventObject;

@SuppressWarnings("serial")
public class ProjectEvent extends EventObject {

	private Page page;
	private Image image;
	private int index;
	private Object oldValue;
	private Object newValue;
	private String propertyName;
	
	public ProjectEvent(Object source) {
		super(source);
	}
	
	public ProjectEvent(Object source, Page page, int index) {
		super(source);
		this.page = page;
		this.index = index;
	}
	
	public ProjectEvent(Object source, Page page, Object oldValue,
			Object newValue, String propertyName) {
		super(source);
		this.page = page;
		this.oldValue = oldValue;
		this.newValue = newValue;
		this.propertyName = propertyName;
	}

	public ProjectEvent(Object source, Image image, int index) {
		super(source);
		this.image = image;
		this.newValue = image.getLabel();
		this.index = index;
	}

	public ProjectEvent(Object source, Image image, Object oldValue,
			Object newValue, String propertyName) {
		super(source);
		this.image = image;
		this.oldValue = oldValue;
		this.newValue = newValue;
		this.propertyName = propertyName;
	}

	public Page getPage() {
		return page;
	}
	
	public Image getImage() {
		return image;
	}
	
	public Object getOldValue() {
		return oldValue;
	}
	
	public Object getNewValue() {
		return newValue;
	}
	
	public int getIndex() {
		return index;
	}
	
	public String getPropertyName() {
		return propertyName;
	}
	
}
