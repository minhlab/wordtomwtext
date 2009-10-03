package com.thuvienkhoahoc.wordtomwtext.ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.thuvienkhoahoc.wordtomwtext.data.Page;

@SuppressWarnings("serial")
public class PnlPageEditor extends JPanel {

	private Page page;
	private boolean dirty;
	private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
	
	public PnlPageEditor(Page page) {
		super();
		this.page = page;
		initComponents();
		setDirty(false);
	}

	private void initComponents() {
		setLayout(new BorderLayout());
		
		txtContent.setFont(Font.getFont("Courier 12"));
		txtContent.setText(page.getText());
		// add listener after setting text to avoid unnecessary events
		txtContent.getDocument().addDocumentListener(new DocumentListener() {
			
			public void removeUpdate(DocumentEvent e) {
				setDirty(true);
			}
			
			public void insertUpdate(DocumentEvent e) {
				setDirty(true);
			}
			
			public void changedUpdate(DocumentEvent e) {
				setDirty(true);
			}
		});
		add(new JScrollPane(txtContent), BorderLayout.CENTER);
	}

	/*
	 * Accessors
	 */
	
	public Page getPage() {
		return page;
	}
	
	public void discard() {
		txtContent.setText(page.getText());
		setDirty(false);
	}
	
	public void save() {
		page.setText(txtContent.getText());
		setDirty(false);
	}
	
	public boolean isDirty() {
		return dirty;
	}
	
	private void setDirty(boolean dirty) {
		if (dirty != this.dirty) {
			boolean oldValue = this.dirty;
			this.dirty = dirty;
			changeSupport.firePropertyChange("dirty", oldValue, dirty);
		}
	}
	
	/*
	 * Listener support
	 * @see java.awt.Container#addPropertyChangeListener(java.beans.PropertyChangeListener)
	 */
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		changeSupport.addPropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		changeSupport.addPropertyChangeListener(propertyName, listener);
	}

	public PropertyChangeListener[] getPropertyChangeListeners() {
		return changeSupport.getPropertyChangeListeners();
	}

	public PropertyChangeListener[] getPropertyChangeListeners(
			String propertyName) {
		return changeSupport.getPropertyChangeListeners(propertyName);
	}

	public boolean hasListeners(String propertyName) {
		return changeSupport.hasListeners(propertyName);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		changeSupport.removePropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		changeSupport.removePropertyChangeListener(propertyName, listener);
	}

	/*
	 * Private components
	 */

	private JTextPane txtContent = new JTextPane();
	
}
