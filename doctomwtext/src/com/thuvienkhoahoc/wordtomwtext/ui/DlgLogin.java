package com.thuvienkhoahoc.wordtomwtext.ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import net.sourceforge.jwbf.bots.util.JwbfException;

import com.thuvienkhoahoc.wordtomwtext.Application;

@SuppressWarnings("serial")
public class DlgLogin extends JDialog {

	public DlgLogin(JFrame parent) {
		super(parent, true);
		initComponents();
		handleEvents();
	}

	private void initComponents() {
		setLocationByPlatform(true);
		setTitle("Đăng nhập");
		getContentPane().setLayout(layoutMain);

		lblSite.setText("Địa chỉ:");
		getContentPane().add(
				lblSite,
				new GridBagConstraints(0, 0, 1, 1, 0, 0,
						GridBagConstraints.EAST, GridBagConstraints.NONE,
						new Insets(5, 5, 0, 3), 0, 0));
		// TODO remove default site
		txtSite.setText("http://thuvienkhoahoc.com/tusach/");
		txtSite.setPreferredSize(new Dimension(150,
				txtSite.getPreferredSize().height));
		getContentPane().add(
				txtSite,
				new GridBagConstraints(1, 0, 1, 1, 1.0, 0,
						GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
						new Insets(5, 0, 0, 5), 0, 0));

		lblUsername.setText("Người dùng:");
		getContentPane().add(
				lblUsername,
				new GridBagConstraints(0, 1, 1, 1, 0, 0,
						GridBagConstraints.EAST, GridBagConstraints.NONE,
						new Insets(0, 5, 0, 3), 0, 0));
		getContentPane().add(
				txtUsername,
				new GridBagConstraints(1, 1, 1, 1, 1.0, 0,
						GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
						new Insets(2, 0, 0, 5), 0, 0));

		lblPassword.setText("Mật khẩu:");
		getContentPane().add(
				lblPassword,
				new GridBagConstraints(0, 2, 1, 1, 0, 0,
						GridBagConstraints.EAST, GridBagConstraints.NONE,
						new Insets(0, 5, 0, 3), 0, 0));
		getContentPane().add(
				txtPassword,
				new GridBagConstraints(1, 2, 1, 1, 1.0, 0,
						GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
						new Insets(2, 0, 0, 5), 0, 0));

		pnlButton.setLayout(layoutButton);

		getRootPane().setDefaultButton(btnOk);
		btnOk.setText("Đăng nhập");
		pnlButton.add(btnOk);

		btnCancel.setText("Thôi");
		pnlButton.add(btnCancel);

		getContentPane().add(
				pnlButton,
				new GridBagConstraints(0, 3, 2, 1, 0, 0,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(2, 5, 5, 5), 0, 0));

		pack();
	}

	private void handleEvents() {
		btnOk.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				onLogin();
			}
		});
		btnCancel.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		});
	}

	protected void onLogin() {
		try {
			Application.getInstance().login(txtSite.getText(),
					txtUsername.getText(),
					String.valueOf(txtPassword.getPassword()));
			setVisible(false);
		} catch (MalformedURLException e) {
			JOptionPane.showMessageDialog(this, "Lỗi đăng nhập",
					"Đường dẫn sai. Mời bạn nhập lại",
					JOptionPane.ERROR_MESSAGE);
			txtSite.selectAll();
			txtSite.requestFocusInWindow();
		} catch (JwbfException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(),
					"Lỗi đăng nhập", JOptionPane.ERROR_MESSAGE);
			txtUsername.selectAll();
			txtUsername.requestFocusInWindow();
		}
	}

	protected void onCancel() {
		setVisible(false);
	}

	private GridBagLayout layoutMain = new GridBagLayout();

	private JLabel lblSite = new JLabel();
	private JTextField txtSite = new JTextField();

	private JLabel lblUsername = new JLabel();
	private JTextField txtUsername = new JTextField();

	private JLabel lblPassword = new JLabel();
	private JPasswordField txtPassword = new JPasswordField();

	private JPanel pnlButton = new JPanel();
	private FlowLayout layoutButton = new FlowLayout(FlowLayout.CENTER);
	private JButton btnOk = new JButton();
	private JButton btnCancel = new JButton();

}
