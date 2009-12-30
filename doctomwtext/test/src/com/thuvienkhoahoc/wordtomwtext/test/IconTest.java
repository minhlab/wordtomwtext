package com.thuvienkhoahoc.wordtomwtext.test;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class IconTest {
public static void main(String[] args) {
	final String IMAGE_DIR = "../images/";
	ImageIcon icon = new ImageIcon(
			IconTest.class.getResource(IMAGE_DIR + "running.png"));
	JLabel label = new JLabel(icon);
	JFrame frame = new JFrame();
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.getContentPane().add(label);
	frame.pack();
	frame.setVisible(true);
}
}
