package com.thuvienkhoahoc.wordtomwtext;

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

import net.sourceforge.jwbf.actions.mw.util.ActionException;
import net.sourceforge.jwbf.bots.MediaWikiBot;

@SuppressWarnings("serial")
public class DlgLogin extends JDialog {

	private MediaWikiBot bot = null;

	public DlgLogin(JFrame parent) {
		super(parent);
		initComponents();
	}

	private void initComponents() {
		setLocationByPlatform(true);
		setModal(true);
		setTitle("\u0110\u0103ng nh\u1EADp");
		getContentPane().setLayout(layoutMain);

		lblSite.setText("\u0110\u1ECBa ch\u1EC9:");
		getContentPane().add(
				lblSite,
				new GridBagConstraints(0, 0, 1, 1, 0, 0,
						GridBagConstraints.EAST, GridBagConstraints.NONE,
						new Insets(5, 5, 0, 3), 0, 0));
		//TODO remove default site
		txtSite.setText("http://thuvienkhoahoc.com/tusach/"); 
		txtSite.setPreferredSize(new Dimension(150, txtSite.getPreferredSize().height));
		getContentPane().add(
				txtSite,
				new GridBagConstraints(1, 0, 1, 1, 1.0, 0,
						GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
						new Insets(5, 0, 0, 5), 0, 0));

		lblUsername.setText("Ng\u01B0\u1EDDi d\u00F9ng:");
		getContentPane().add(
				lblUsername,
				new GridBagConstraints(0, 1, 1, 1, 0, 0,
						GridBagConstraints.EAST, GridBagConstraints.NONE,
						new Insets(0, 5, 0, 3), 0, 0));
		//TODO remove default user
		txtUsername.setText("Cumeo89");
		getContentPane().add(
				txtUsername,
				new GridBagConstraints(1, 1, 1, 1, 1.0, 0,
						GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
						new Insets(2, 0, 0, 5), 0, 0));

		lblPassword.setText("M\u1EADt kh\u1EA9u:");
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
		btnOk.setText("\u0110\u0103ng nh\u1EADp");
		btnOk.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				onLogin();
			}
		});
		pnlButton.add(btnOk);
		
		btnCancel.setText("Tho\u00E1t");
		btnCancel.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		});
		pnlButton.add(btnCancel);

		getContentPane().add(
				pnlButton,
				new GridBagConstraints(0, 3, 2, 1, 0, 0,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(2, 5, 5, 5), 0, 0));
		
		pack();
	}

	protected void onCancel() {
		bot = null;
		setVisible(false);
	}

	protected void onLogin() {
		try {
			bot = new MediaWikiBot(txtSite.getText());
			bot.login(txtUsername.getText(), 
					String.valueOf(txtPassword.getPassword()));
			setVisible(false);
		} catch (MalformedURLException e) {
			JOptionPane.showMessageDialog(this, "\u0110\u01B0\u1EDDng d\u1EABn sai. M\u1EDDi b\u1EA1n nh\u1EADp l\u1EA1i");
			txtSite.selectAll();
			txtSite.requestFocusInWindow();
		} catch (ActionException e) {
			JOptionPane.showMessageDialog(this, "Kh\u00F4ng \u0111\u0103ng nh\u1EADp \u0111\u01B0\u1EE3c. C\u00F3 th\u1EC3 ng\u01B0\u1EDDi d\u00F9ng v\u00E0/ho\u1EB7c m\u1EADt kh\u1EA9u c\u1EE7a b\u1EA1n kh\u00F4ng ch\u00EDnh x\u00E1c");
			txtUsername.selectAll();
			txtUsername.requestFocusInWindow();
		}
	}
	
	public String getUsername() {
		return txtUsername.getText();
	}

	public MediaWikiBot getBot() {
		return bot;
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
