package com.thuvienkhoahoc.wordtomwtext.ui;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public abstract class PnlEditor extends JPanel {

	protected boolean dirty = false;
	private Object object = new Object();

	public PnlEditor(Object object) {
		this.object = object;
	}

	public Object getObject() {
		return object;
	}
	
	public boolean isDirty() {
		return dirty;
	}

	protected abstract void updateLabel();

	protected void setDirty(boolean dirty) {
		if (dirty != this.dirty) {
			this.dirty = dirty;
			updateLabel();
		}
	}

	public abstract void save();

	public abstract void discard();

}