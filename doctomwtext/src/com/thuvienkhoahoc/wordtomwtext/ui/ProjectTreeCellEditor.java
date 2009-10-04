package com.thuvienkhoahoc.wordtomwtext.ui;

import java.awt.Component;
import java.util.EventObject;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.TreePath;

import com.thuvienkhoahoc.wordtomwtext.data.Image;
import com.thuvienkhoahoc.wordtomwtext.data.Page;

public class ProjectTreeCellEditor extends DefaultTreeCellEditor {

	public ProjectTreeCellEditor(JTree tree) {
		super(tree, new ProjectTreeCellRenderer());
	}

	@Override
	public Component getTreeCellEditorComponent(JTree tree, Object value,
			boolean isSelected, boolean expanded, boolean leaf, int row) {
		if (value instanceof Page) {
			Page page = (Page) value;
			value = page.getShortLabel();
		} else if (value instanceof Image) {
			Image image = (Image) value;
			value = image.getLabel();
		}
		return super.getTreeCellEditorComponent(tree, value, isSelected, expanded,
				leaf, row);
	}
	
	@Override
	public boolean isCellEditable(EventObject event) {
		boolean ret = super.isCellEditable(event);
		if (ret) {
			TreePath path = tree.getSelectionPath();
			if (path != null) {
				Object comp = path.getLastPathComponent();
				ret = (comp instanceof Page) || (comp instanceof Image);
			}
		}
		return ret;
	}

}
