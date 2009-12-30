package com.thuvienkhoahoc.wordtomwtext.test;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import com.thuvienkhoahoc.wordtomwtext.ui.PnlFileChooser;

public class FileChooserTester {

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		PnlFileChooser fc = new PnlFileChooser();
		frame.getContentPane().add(fc, BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);
	}
	
}
