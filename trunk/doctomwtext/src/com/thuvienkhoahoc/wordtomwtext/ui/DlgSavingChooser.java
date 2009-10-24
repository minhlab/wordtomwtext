package com.thuvienkhoahoc.wordtomwtext.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

@SuppressWarnings("serial")
public class DlgSavingChooser extends JDialog {

	private Object[] selectedValues = null;

	public DlgSavingChooser(JFrame owner) {
		super(owner, true);
		initComponents();
		handleEvents();
	}

	private void initComponents() {
		this.getRootPane().setDefaultButton(btnOk);
		this.setTitle("Chọn lưu tài liệu");
		getContentPane().setLayout(new GridBagLayout());

		lblInfo
				.setText("Tài liệu cần được lưu trước khi tiến hành cập nhật liên kết.");
		this.getContentPane().add(
				lblInfo,
				new GridBagConstraints(0, 0, 1, 1, 0, 0,
						GridBagConstraints.WEST, GridBagConstraints.NONE,
						new Insets(3, 3, 0, 3), 0, 0));

		lblInfo2.setText("Bạn hãy chọn những tài liệu muốn lưu:");
		this.getContentPane().add(lblInfo2,
				new GridBagConstraints(0, 1, 1, 1, 0, 0,
						GridBagConstraints.WEST, GridBagConstraints.NONE,
						new Insets(2, 3, 0, 3), 0, 0));

		lstEditors.setModel(modEditors);
		this.getContentPane().add(new JScrollPane(lstEditors),
				new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(7, 3, 0, 3), 0, 0));

		btnOk.setText("Tiếp tục");
		pnlButtons.add(btnOk);

		btnCancel.setText("Thôi");
		pnlButtons.add(btnCancel);

		this.getContentPane().add(pnlButtons,
				new GridBagConstraints(0, 3, 1, 1, 1.0, 0,
						GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL,
						new Insets(5, 3, 3, 3), 0, 0));

		pack();
	}

	private void handleEvents() {
		btnOk.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				selectedValues = lstEditors.getSelectedValues();
				setVisible(false);
			}
		});

		btnCancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				selectedValues = null;
				setVisible(false);
			}
		});
	}

	public Object[] loadObjectsAndShow(Object[] objs) {
		modEditors.clear();
		for (Object object : objs) {
			modEditors.addElement(object);
		}
		lstEditors.setSelectionInterval(0, modEditors.getSize() - 1);
		setVisible(true);
		return getSelectedObjects();
	}

	public Object[] getSelectedObjects() {
		return selectedValues;
	}

	private JLabel lblInfo = new JLabel();
	private JLabel lblInfo2 = new JLabel();
	private DefaultListModel modEditors = new DefaultListModel();
	private JList lstEditors = new JList();
	private JPanel pnlButtons = new JPanel();
	private JButton btnOk = new JButton();
	private JButton btnCancel = new JButton();

}
