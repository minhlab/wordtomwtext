package com.thuvienkhoahoc.wordtomwtext.ui;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public abstract class AbstractFunctionalPanel extends JPanel {
	
	public static final int STATE_READY = 0; 
	public static final int STATE_ERROR = 1; 
	public static final int STATE_RUNNING = 2; 
	public static final int STATE_FINISHED = 3; 
	
	private int state = STATE_READY;
	
	public abstract void load(Object data);
	
	public abstract Object getResult();
	
	public boolean next() {
		return true;
	}
	
	public int getState() {
		return state;
	}
	
	protected void setState(int state) {
		if (this.state != state) {
			int oldValue = this.state;
			this.state = state;
			firePropertyChange("state", oldValue, state);
		}
	}
	
	public boolean canBack() {
		return state == STATE_READY || state == STATE_ERROR;
	}
	
	public boolean canNext() {
		return state == STATE_READY || state == STATE_FINISHED;
	}
	
	public boolean canClose() {
		return state != STATE_RUNNING;
	}
	
}
