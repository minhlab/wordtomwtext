package com.thuvienkhoahoc.wordtomwtext.ui;

import java.awt.Component;

import javax.swing.Icon;
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
		String text = value.toString();
		Icon icon = null;
		if (value instanceof Page) {
			Page page = (Page) value;
			text = page.getShortLabel();
			//TODO change icon
		} else if (value instanceof Image) {
			Image image = (Image) value;
			text = image.getLabel();
			//TODO change icon
		}
		JLabel label = (JLabel) super.getTreeCellRendererComponent(tree, text,
				sel, expanded, leaf, row, hasFocus);
		if (icon != null) {
			label.setIcon(icon);
		}
		return label;
	}

}
