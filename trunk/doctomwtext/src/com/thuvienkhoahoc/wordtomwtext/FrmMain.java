package com.thuvienkhoahoc.wordtomwtext;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.HeadlessException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;

public class FrmMain extends JFrame {

	// GUI items
	JToolBar toolbar = new JToolBar();
	JLabel lblSignIn = new JLabel();
	
	JPanel pnlMainWrapper = new JPanel();
	CardLayout layoutMainWrapper = new CardLayout();
	
	JPanel pnlWord = new JPanel();
	
	public FrmMain() throws HeadlessException {
		super();
		initComponents();
	}

	private void initComponents() {
		lblSignIn.setText("Đăng nhập");
		toolbar.add(lblSignIn);
		
		
		
		getContentPane().add(toolbar, BorderLayout.NORTH);
		
		
	}
	
}
