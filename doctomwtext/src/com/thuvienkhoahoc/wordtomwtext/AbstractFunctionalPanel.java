package com.thuvienkhoahoc.wordtomwtext;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public abstract class AbstractFunctionalPanel extends JPanel {
	
	public abstract void load();
	
	public abstract boolean work();
	
}
