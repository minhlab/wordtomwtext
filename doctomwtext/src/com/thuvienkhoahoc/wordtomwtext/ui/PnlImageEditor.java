package com.thuvienkhoahoc.wordtomwtext.ui;

import javax.swing.JTabbedPane;

import com.thuvienkhoahoc.wordtomwtext.data.Image;
import com.thuvienkhoahoc.wordtomwtext.data.ProjectAdapter;
import com.thuvienkhoahoc.wordtomwtext.data.ProjectEvent;

@SuppressWarnings("serial")
public class PnlImageEditor extends PnlEditor {

	private final JTabbedPane tabbedPane;
	
	public PnlImageEditor(final JTabbedPane tabbedPane, Image image) {
		super(image);
		this.tabbedPane = tabbedPane;
		image.getProject().addProjectListener(new ProjectAdapter() {
			@Override
			public void imagePropertyChanged(ProjectEvent evt) {
				if ("label".equals(evt.getPropertyName())) {
					onLabelChanged();
				}
			}
		});		
		initComponents();
	}

	private void initComponents() {
		// TODO Auto-generated method stub
	}

	protected void onLabelChanged() {
		updateLabel();
	}

	@Override
	public void discard() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void save() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Image getObject() {
		// TODO Auto-generated method stub
		return (Image) super.getObject();
	}
	
	@Override
	protected void updateLabel() {
		String title = getObject().getLabel();
		if (dirty) {
			title = "*" + title;
		}
		tabbedPane.setTitleAt(tabbedPane.indexOfComponent(this), title);
	}

	/*
	 * Components
	 */

}
