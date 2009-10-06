package com.thuvienkhoahoc.wordtomwtext.ui;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.thuvienkhoahoc.wordtomwtext.data.Page;
import com.thuvienkhoahoc.wordtomwtext.data.ProjectAdapter;
import com.thuvienkhoahoc.wordtomwtext.data.ProjectEvent;

@SuppressWarnings("serial")
public class PnlPageEditor extends PnlEditor {

	private final JTabbedPane tabbedPane;
	private JTextPane txtContent = new JTextPane();

	public PnlPageEditor(final JTabbedPane tabbedPane, Page page) {
		super(page);
		this.tabbedPane = tabbedPane;
		initComponents();
		handleEvent();
	}

	private void initComponents() {
		setLayout(new BorderLayout());

		txtContent.setFont(Font.getFont("Courier 12"));
		add(new JScrollPane(txtContent), BorderLayout.CENTER);
	}

	private void handleEvent() {
		getObject().getProject().addProjectListener(new ProjectAdapter() {
			@Override
			public void pagePropertyChanged(ProjectEvent evt) {
				if (!getObject().equals(evt.getPage())) {
					return;
				}
				if ("label".equals(evt.getPropertyName())) {
					updateLabel();
				} else if ("markedForRemoval".equals(evt.getPropertyName())) {
					updateRemovalStatus();
				}
			}
		});
		
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
	}

	@Override
	public Page getObject() {
		return (Page) super.getObject();
	}

	protected void updateRemovalStatus() {
		txtContent.setEditable(!getObject().isMarkedForRemoval());
	}

	@Override
	public void load() {
		txtContent.setText(getObject().getText());
		updateRemovalStatus();
		setDirty(false);
	}

	@Override
	public void save() {
		getObject().setText(txtContent.getText());
		setDirty(false);
	}

	protected void updateLabel() {
		String title = getObject().getLabel();
		if (dirty) {
			title = "*" + title;
		}
		tabbedPane.setTitleAt(tabbedPane.indexOfComponent(this), title);
		this.setName(title);
	}

}
