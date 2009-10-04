package com.thuvienkhoahoc.wordtomwtext.ui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileFilter;

import com.thuvienkhoahoc.wordtomwtext.data.Project;
import com.thuvienkhoahoc.wordtomwtext.logic.Converter;

@SuppressWarnings("serial")
public class PnlFileChooser extends AbstractFunctionalPanel {

	private Project project;
	private Converter converter = new Converter();

	public PnlFileChooser() {
		setLayout(layout);

		realChooser.setFileFilter(new FileFilter() {

			@Override
			public String getDescription() {
				return "Tài liệu Microsoft Word 97/2000";
			}

			@Override
			public boolean accept(File f) {
				if (f.isDirectory()) {
					return true;
				}
				return f.getName().endsWith(".doc");
			}
		});
		realChooser.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (JFileChooser.APPROVE_SELECTION.equals(e.getActionCommand())) {
					onAddFile();
				}
			}
		});
		realChooser.setControlButtonsAreShown(false);
		realChooser.setMultiSelectionEnabled(true);
		add(realChooser, BorderLayout.CENTER);

		pnlFiles.setLayout(new GridBagLayout());

		btnAdd.setText("Thêm");
		btnAdd.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				onAddFile();
			}
		});
		pnlFiles.add(btnAdd, new GridBagConstraints(0, 0, 1, 1, 0, 0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,
						3, 0, 0), 0, 0));

		btnRemove.setText("Bớt");
		btnRemove.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				onRemoveFile();
			}
		});
		pnlFiles.add(btnRemove, new GridBagConstraints(1, 0, 1, 1, 0, 0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,
						2, 0, 5), 0, 0));

		tblFiles.setModel(modFiles);
		tblFiles.createDefaultColumnsFromModel();
		tblFiles.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		tblFiles.getColumnModel().getColumn(0).setMinWidth(150);
		pnlFiles.add(new JScrollPane(tblFiles), new GridBagConstraints(0, 1, 2,
				1, 1.0, 1.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(3, 3, 5, 3), 0, 0));

		add(pnlFiles, BorderLayout.EAST);
	}

	protected void onRemoveFile() {
		int[] selectedRows = tblFiles.getSelectedRows();
		Arrays.sort(selectedRows); // make sure that it is sorted ascendingly
		for (int i = selectedRows.length - 1; i >= 0; i--) {
			modFiles.remove(selectedRows[i]);
		}
	}

	protected void onAddFile() {
		File[] selectedFiles = realChooser.getSelectedFiles();
		for (int i = 0; i < selectedFiles.length; i++) {
			if (!modFiles.contains(selectedFiles[i])) {
				modFiles.add(selectedFiles[i]);
			}
		}
	}

	@Override
	public void load(Object data) {
		modFiles.clear();
	}

	@Override
	public boolean next() {
		if (modFiles.getFileList().size() <= 0) {
			JOptionPane
					.showMessageDialog(
							this,
							"Xin hãy chọn những tệp muốn chuyển đổi và nhấn \"Tiếp tục\"",
							"Bạn chưa chọn tệp nào", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		try {
			project = converter.convert(modFiles.getFileList());
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this,
					"Xin hãy kiểm tra lại những tệp được chọn.",
					"Có lỗi khi đọc tệp", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return true;
	}

	@Override
	public Project getResult() {
		return project;
	}

	/*
	 * Components
	 */
	private BorderLayout layout = new BorderLayout();
	private JFileChooser realChooser = new JFileChooser();
	private JTable tblFiles = new JTable();
	private JPanel pnlFiles = new JPanel();
	private JButton btnAdd = new JButton();
	private JButton btnRemove = new JButton();
	private FileTableModel modFiles = new FileTableModel();

}
