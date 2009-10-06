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
import javax.swing.filechooser.FileNameExtensionFilter;

import com.thuvienkhoahoc.wordtomwtext.data.Project;
import com.thuvienkhoahoc.wordtomwtext.logic.Converter;

@SuppressWarnings("serial")
public class PnlFileChooser extends AbstractFunctionalPanel {

	private Project project;
	private Converter converter = new Converter();

	public PnlFileChooser() {
		initComponents();
		handleEvents();
	}

	private void initComponents() {
		setLayout(new BorderLayout());

		realChooser.setFileFilter(new FileNameExtensionFilter(
				"Tài liệu Microsoft Word 97/2000", "doc"));
		realChooser.setControlButtonsAreShown(false);
		realChooser.setMultiSelectionEnabled(true);
		add(realChooser, BorderLayout.CENTER);

		pnlFiles.setLayout(new GridBagLayout());

		btnAdd.setText("Thêm");
		pnlFiles.add(btnAdd, new GridBagConstraints(0, 0, 1, 1, 0, 0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,
						3, 0, 0), 0, 0));

		btnRemove.setText("Bớt");
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

	private void handleEvents() {
		realChooser.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (JFileChooser.APPROVE_SELECTION.equals(e.getActionCommand())) {
					addSelectedFiles();
				}
			}
		});
		btnAdd.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				addSelectedFiles();
			}
		});
		btnRemove.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				removeSelectedFiles();
			}
		});
	}

	protected void addSelectedFiles() {
		File[] selectedFiles = realChooser.getSelectedFiles();
		for (int i = 0; i < selectedFiles.length; i++) {
			if (!modFiles.contains(selectedFiles[i])) {
				modFiles.add(selectedFiles[i]);
			}
		}
	}

	protected void removeSelectedFiles() {
		int[] selectedRows = tblFiles.getSelectedRows();
		Arrays.sort(selectedRows); // make sure that it is sorted ascendingly
		for (int i = selectedRows.length - 1; i >= 0; i--) {
			modFiles.remove(selectedRows[i]);
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
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi: "
					+ ex.getMessage()
					+ ". Xin hãy kiểm tra lại những tệp được chọn.",
					"Có lỗi khi đọc tệp", JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
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
	private JFileChooser realChooser = new JFileChooser();
	private JTable tblFiles = new JTable();
	private JPanel pnlFiles = new JPanel();
	private JButton btnAdd = new JButton();
	private JButton btnRemove = new JButton();
	private FileTableModel modFiles = new FileTableModel();

}
