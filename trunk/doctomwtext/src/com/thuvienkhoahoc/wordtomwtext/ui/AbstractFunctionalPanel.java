package com.thuvienkhoahoc.wordtomwtext.ui;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public abstract class AbstractFunctionalPanel extends JPanel {
	
	public abstract void load(Object data);
	
	public abstract Object getResult();
	
	public abstract boolean next();
	
}
