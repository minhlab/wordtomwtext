package com.thuvienkhoahoc.wordtomwtext.ui;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellEditor;

import com.thuvienkhoahoc.wordtomwtext.data.Image;
import com.thuvienkhoahoc.wordtomwtext.data.Page;

public class ProjectTreeCellEditor extends DefaultTreeCellEditor {

//	private Object value;

	public ProjectTreeCellEditor(JTree tree) {
		super(tree, new ProjectTreeCellRenderer());
	}

	@Override
	public Component getTreeCellEditorComponent(JTree tree, Object value,
			boolean isSelected, boolean expanded, boolean leaf, int row) {
//		this.value = value;
		if (value instanceof Page) {
			Page page = (Page) value;
			value = page.getShortLabel();
		} else if (value instanceof Image) {
			Image image = (Image) value;
			value = image.getLabel();
		}
		return super.getTreeCellEditorComponent(tree, value, isSelected,
				expanded, leaf, row);
	}

	/*
	 * Do việc override hàm stopCellEditing vô tác dụng gây khó khăn khi
	 * muốn validate tên trang, tên ảnh do người dùng nhập vào. Chúng ta
	 * đổi sang cách dùng popup dialog.
	 * 
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

	@Override
	public boolean stopCellEditing() {
		Project project = ((ProjectTreeModel) tree.getModel()).getProject();
		try {
			if (value instanceof Page) {
				Page page = (Page) value;
				project.checkPageTitle(getCellEditorValue().toString(), page);
			} else if (value instanceof Image) {
				Image image = (Image) value;
				project.checkImageTitle(getCellEditorValue().toString(), image);
			}
		} catch (DuplicatedTitleException e) {
			JOptionPane.showMessageDialog(editingComponent, "Tên \""
					+ e.getTitle()
					+ "\" đã được sử dụng, bạn hãy chọn tên khác.",
					"Lỗi trùng tên", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return super.stopCellEditing();
	}
	*/
}
