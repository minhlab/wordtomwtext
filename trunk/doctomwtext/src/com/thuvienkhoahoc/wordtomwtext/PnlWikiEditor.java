package com.thuvienkhoahoc.wordtomwtext;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JTabbedPane;

@SuppressWarnings("serial")
public class PnlWikiEditor extends AbstractFunctionalPanel {

	public PnlWikiEditor() {
		initComponents();
	}

	private void initComponents() {
		setLayout(new BorderLayout());

		add(pnlMain, BorderLayout.CENTER);
	}

	@Override
	public void load() {
		pnlMain.removeAll();
		for (int i = 0; i < Application.getInstance().getPages().size(); i++) {
			Page page = Application.getInstance().getPages().get(i);
			addPageTab(page);
		}
	}

	private void addPageTab(Page page) {
		PnlPageEditor pageEditor = new PnlPageEditor(page);
		pageEditor.addPropertyChangeListener(new PropertyChangeListener() {

			public void propertyChange(PropertyChangeEvent evt) {
				if ("dirty".equals(evt.getPropertyName())) {
					onDirtyChanged((PnlPageEditor) evt.getSource(),
							((Boolean) evt.getNewValue()).booleanValue());
				}
			}
		});
		pnlMain.addTab(page.getTitle(), pageEditor);
	}

	protected void onDirtyChanged(PnlPageEditor source, boolean dirty) {
		int index = pnlMain.indexOfComponent(source);
		String title = pnlMain.getTitleAt(index);
		if (title.endsWith("*")) {
			title = title.substring(0, title.length() - 1);
		}
		if (dirty) {
			title = title + "*";
		}
		pnlMain.setTitleAt(index, title);
	}

	@Override
	public boolean work() {
		Application.getInstance().getPages().clear();
		for (int i = 0; i < pnlMain.getTabCount(); i++) {
			if (!(pnlMain.getTabComponentAt(i) instanceof PnlPageEditor)) {
				continue;
			}
			Application.getInstance().getPages().add(
					((PnlPageEditor) pnlMain.getTabComponentAt(i)).getPage());
		}
		return true;
	}

	private JTabbedPane pnlMain = new JTabbedPane();

}
