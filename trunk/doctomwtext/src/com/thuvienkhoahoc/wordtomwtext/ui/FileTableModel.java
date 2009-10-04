/**
 * 
 */
package com.thuvienkhoahoc.wordtomwtext.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

@SuppressWarnings("serial")
public class FileTableModel extends AbstractTableModel {

	private List<File> fileList;

	public FileTableModel() {
		this(new ArrayList<File>());
	}

	public FileTableModel(List<File> fileList) {
		super();
		this.fileList = fileList;
	}

	public List<File> getFileList() {
		return fileList;
	}
	
	/**
	 * @param e
	 * @return
	 * @see java.util.List#add(java.lang.Object)
	 */
	public boolean add(File e) {
		boolean ret = fileList.add(e);
		fireTableDataChanged();
		return ret;
	}

	/**
	 * 
	 * @see java.util.List#clear()
	 */
	public void clear() {
		fileList.clear();
		fireTableDataChanged();
	}

	/**
	 * @param o
	 * @return
	 * @see java.util.List#contains(java.lang.Object)
	 */
	public boolean contains(Object o) {
		return fileList.contains(o);
	}

	/**
	 * @param o
	 * @return
	 * @see java.util.List#remove(java.lang.Object)
	 */
	public boolean remove(Object o) {
		boolean ret = fileList.remove(o);
		fireTableDataChanged();
		return ret;
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
		return fileList.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		File file = fileList.get(rowIndex);
		switch (columnIndex) {
		case 0:
			return file.getName();
		case 1:
			return file.getAbsolutePath();
		}
		return null;
	}

}