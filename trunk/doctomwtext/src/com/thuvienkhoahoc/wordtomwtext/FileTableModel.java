/**
 * 
 */
package com.thuvienkhoahoc.wordtomwtext;

import java.io.File;
import java.util.List;

import javax.swing.table.AbstractTableModel;

@SuppressWarnings("serial")
public class FileTableModel extends AbstractTableModel {

	private List<File> files;

	public FileTableModel(List<File> files) {
		super();
		this.files = files;
	}

	@Override
	public String getColumnName(int column) {
		switch (column) {
		case 0:
			return "Tên";
		case 1:
			return "Đường dẫn";
		}
		return super.getColumnName(column);
	}
	
	public int getColumnCount() {
		return 2;
	}

	public int getRowCount() {
		return files.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		File file = files.get(rowIndex);
		switch (columnIndex) {
		case 0:
			return file.getName();
		case 1:
			return file.getAbsolutePath();
		}
		return null;
	}

}