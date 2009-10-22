package com.thuvienkhoahoc.wordtomwtext.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class DlgArticleRename extends JDialog {

	public static final int REFACTOR_OPTION = 2;
	public static final int SAVE_OPTION = 1;
	public static final int CANCEL_OPTION = 0;

	private int option = CANCEL_OPTION;

	public DlgArticleRename(Component parentComponent) {
		super(JOptionPane.getFrameForComponent(parentComponent), true);
		initComponents();
		handleEvents();
	}

	private void initComponents() {
		final JPanel pnlWrapper = new JPanel();
		pnlWrapper.setLayout(new BorderLayout(0, 5));
		pnlWrapper.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		getContentPane().add(pnlWrapper, BorderLayout.CENTER);

		pnlWrapper.add(txtNewName, BorderLayout.CENTER);

		pnlButtons.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 0));

		btnRefactor.setText("Lưu và Cập nhật LK");
		pnlButtons.add(btnRefactor);

		btnSave.setText("Lưu");
		pnlButtons.add(btnSave);

		btnCancel.setText("Hủy bỏ");
		pnlButtons.add(btnCancel);

		pnlWrapper.add(pnlButtons, BorderLayout.SOUTH);
		
		pack();
	}

	private void handleEvents() {
		btnRefactor.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				acceptOption(REFACTOR_OPTION);
			}
		});
		btnSave.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				acceptOption(SAVE_OPTION);
			}
		});
		btnCancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				acceptOption(CANCEL_OPTION);
			}
		});
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent e) {
				txtNewName.requestFocusInWindow();
			}
		});
	}

	public String getInputValue() {
		return txtNewName.getText();
	}

	public int getSelectedOption() {
		return option;
	}

	private void acceptOption(int option) {
		this.option = option;
		setVisible(false);
	}

	public int setupAndShow(String title, String initialValue) {
		setTitle(title);
		txtNewName.setText(initialValue);
		setVisible(true);
		return getSelectedOption();
	}

	static Window getWindowForComponent(Component parentComponent)
			throws HeadlessException {
		if (parentComponent == null)
			return JOptionPane.getRootFrame();
		if (parentComponent instanceof Frame
				|| parentComponent instanceof Dialog)
			return (Window) parentComponent;
		return getWindowForComponent(parentComponent.getParent());
	}

	private JTextField txtNewName = new JTextField();
	private JPanel pnlButtons = new JPanel();
	private JButton btnRefactor = new JButton();
	private JButton btnSave = new JButton();
	private JButton btnCancel = new JButton();

}
