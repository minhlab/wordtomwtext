package com.thuvienkhoahoc.wordtomwtext.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;

import com.thuvienkhoahoc.wordtomwtext.data.Page;
import com.thuvienkhoahoc.wordtomwtext.data.Project;
import com.thuvienkhoahoc.wordtomwtext.logic.Converter;

@SuppressWarnings("serial")
public class PnlWikiEditor extends AbstractFunctionalPanel<List<File>, Project> {

	private Project project = new Project();
	private Converter converter = new Converter();
	
	public PnlWikiEditor() {
		initComponents();
	}

	private void initComponents() {
		setLayout(new BorderLayout());
	
		treeProject.setBorder(BorderFactory.createEtchedBorder());
		treeProject.setModel(modProject);
		treeProject.setCellRenderer(new ProjectTreeCellRenderer());
		JScrollPane scrProject = new JScrollPane(treeProject);
		scrProject.setPreferredSize(new Dimension(280, 500));
		add(scrProject, BorderLayout.WEST);

		add(pnlMain, BorderLayout.CENTER);
	}

	@Override
	public void load(List<File> files) {
		try {
			project = converter.convert(files);
			modProject.setProject(project);
			// pre-open tabs for main pages only
			pnlMain.removeAll();
			for (Page page : project.getPages()) {
				addPageTab(page);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		pnlMain.addTab(page.getLabel(), pageEditor);
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
	public boolean next() {
		return false;
	}

	@Override
	public Project getResult() {
		return project;
	}
	
	private JTabbedPane pnlMain = new JTabbedPane();
	private JTree treeProject = new JTree();
	private ProjectTreeModel modProject = new ProjectTreeModel();

}
