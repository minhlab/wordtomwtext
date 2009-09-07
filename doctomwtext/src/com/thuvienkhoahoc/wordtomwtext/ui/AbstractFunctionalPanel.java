package com.thuvienkhoahoc.wordtomwtext.ui;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public abstract class AbstractFunctionalPanel<I,O> extends JPanel {
	
	public abstract void load(I data);
	
	public abstract O getResult();
	
	public abstract boolean next();
	
}
