package com.thuvienkhoahoc.wordtomwtext.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.thuvienkhoahoc.wordtomwtext.data.Image;

@SuppressWarnings("serial")
public class PnlImageEditor extends JPanel {

	Image image;

	public PnlImageEditor(Image image) {
		super();
		this.image = image;
		initComponents();
	}

	private void initComponents() {
		// TODO Auto-generated method stub
		lblLabel.setText("Tên: ");

		lblLabelChange.setText("thay đổi");
		lblLabelChange.setForeground(Color.BLUE);
		lblLabelChange
				.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lblLabelChange.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					onChangeLabel();
				}
			}
		});
	}

	protected void onChangeLabel() {
		// TODO Auto-generated method stub

	}

	/*
	 * Components
	 */
	private JLabel lblLabel = new JLabel();
	private JLabel txtLabel = new JLabel();
	private JLabel lblLabelChange = new JLabel();

}
