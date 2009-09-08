package com.thuvienkhoahoc.wordtomwtext.ui;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import com.thuvienkhoahoc.wordtomwtext.data.Image;
import com.thuvienkhoahoc.wordtomwtext.data.Page;

@SuppressWarnings("serial")
public class ProjectTreeCellRenderer extends DefaultTreeCellRenderer {

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		if (value instanceof Page) {
			Page page = (Page) value;
			JLabel label = (JLabel) super.getTreeCellRendererComponent(tree,
					page.getShortLabel(), sel, expanded, leaf, row, hasFocus);
			// label.setIcon(icon);
			return label;
		}
		if (value instanceof Image) {
			Image image = (Image) value;
			JLabel label = (JLabel) super.getTreeCellRendererComponent(tree,
					image.getLabel(), sel, expanded, leaf, row, hasFocus);
			// label.setIcon(icon);
			return label;
		}
		return super.getTreeCellRendererComponent(tree, value, sel, expanded,
				leaf, row, hasFocus);
	}

}
