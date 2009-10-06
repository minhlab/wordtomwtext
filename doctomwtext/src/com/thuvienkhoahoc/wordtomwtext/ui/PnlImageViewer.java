package com.thuvienkhoahoc.wordtomwtext.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class PnlImageViewer extends JPanel {

	private BufferedImage image;
	private Dimension preferredSize = new Dimension();
	private double reversedScale = 0.5;

	public PnlImageViewer() {
		super();
	}

	@Override
	protected void paintComponent(Graphics g) {
		Rectangle b = g.getClipBounds();
		int dx2 = b.x + b.width;
		int dy2 = b.y + b.height;
		g.drawImage(image, b.x, b.y, dx2, dy2, (int) (b.x * reversedScale),
				(int) (b.y * reversedScale), (int) (dx2 * reversedScale),
				(int) (dy2 * reversedScale), null);
	}

	public int getZoom() {
		return (int) (1 / reversedScale * 100);
	}

	public void setZoom(int zoom) {
		this.reversedScale = 100.0 / zoom;
		updatePreferedSize();
	}

	public BufferedImage getImage() {
		return image;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
		updatePreferedSize();
	}

	private void updatePreferedSize() {
		preferredSize.width = (int) (image.getWidth() / reversedScale);
		preferredSize.height = (int) (image.getHeight() / reversedScale);
	}
	
	@Override
	public Dimension getPreferredSize() {
		return preferredSize;
	}

//	@Override
//	public Dimension getMinimumSize() {
//		return preferredSize;
//	}
//	
//	@Override
//	public Dimension getMaximumSize() {
//		return preferredSize;
//	}
	
}
