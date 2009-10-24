package com.thuvienkhoahoc.wordtomwtext.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.RepaintManager;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.thuvienkhoahoc.wordtomwtext.data.Image;
import com.thuvienkhoahoc.wordtomwtext.data.ProjectAdapter;
import com.thuvienkhoahoc.wordtomwtext.data.ProjectEvent;

@SuppressWarnings("serial")
public class PnlImageEditor extends PnlEditor {

	private final JTabbedPane tabbedPane;

	public PnlImageEditor(final JTabbedPane tabbedPane, Image image) {
		super(image);
		this.tabbedPane = tabbedPane;
		initComponents();
		handleEvent();
	}

	private void initComponents() {
		this.setLayout(new BorderLayout());

		pnlToolbar.setLayout(new FlowLayout(FlowLayout.LEFT));

		lblZoom.setText("Zoom");
		pnlToolbar.add(lblZoom);

		cmbZoom.setModel(modZoom);
		cmbZoom.setEditable(true);
		pnlToolbar.add(cmbZoom);

		lblPercent.setText("%");
		pnlToolbar.add(lblPercent);

		btnZoomIn.setText("Phóng to");
		pnlToolbar.add(btnZoomIn);

		btnZoomOut.setText("Thu nhỏ");
		pnlToolbar.add(btnZoomOut);

		btnChange.setText("Thay đổi...");
		pnlToolbar.add(btnChange);

		this.add(pnlToolbar, BorderLayout.NORTH);

		this.add(scrViewer, BorderLayout.CENTER);

		imageChooser.setFileFilter(new FileNameExtensionFilter("Tập tin ảnh",
				"png", "gif", "jpg", "jpeg"));
	}

	private void handleEvent() {
		getObject().getProject().addProjectListener(new ProjectAdapter() {
			@Override
			public void imagePropertyChanged(ProjectEvent evt) {
				if ("label".equals(evt.getPropertyName())) {
					labelUpdated();
				}
			}
		});
		cmbZoom.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				updateZoomValue();
			}
		});
		btnZoomIn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				zoomIn();
			}
		});
		btnZoomOut.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				zoomOut();
			}
		});
		btnChange.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				showImageChooser();
			}
		});
	}

	protected void zoomOut() {
		int index = Arrays.binarySearch(defaultZoomValues, pnlViewer.getZoom());
		index = (index < 0 ? -index - 2 : index - 1);
		if (index < 0) {
			index = 0;
		}
		updateZoomValue(defaultZoomValues[index]);
	}

	protected void zoomIn() {
		int index = Arrays.binarySearch(defaultZoomValues, pnlViewer.getZoom());
		index = (index < 0 ? -index : index + 1);
		if (index >= defaultZoomValues.length) {
			index = defaultZoomValues.length - 1;
		}
		updateZoomValue(defaultZoomValues[index]);
	}

	protected int getPreferredZoomValue() {
		int imageWidth = pnlViewer.getImage().getWidth();
		int viewWidth = (scrViewer.getWidth() <= 0 ? 600 : scrViewer.getWidth());
		int imageHeight = pnlViewer.getImage().getHeight();
		int viewHeight = (scrViewer.getHeight() <= 0 ? 500 : scrViewer
				.getHeight());

		if (imageWidth > viewWidth || imageHeight > viewHeight) {
			int bestFitZoom = (int) (Math.min(viewWidth / (double) imageWidth,
					viewHeight / (double) imageHeight) * 100);
			int index = Arrays.binarySearch(defaultZoomValues, bestFitZoom);
			index = (index < 0 ? -index - 2 : index - 1);
			if (index < 0) {
				index = 0;
			}
			return defaultZoomValues[index];
		}

		return 100;
	}

	protected void updateZoomValue(int value) {
		cmbZoom.setSelectedItem(value);
		updateZoomValue();
	}

	protected void updateZoomValue() {
		try {
			Integer.parseInt(cmbZoom.getSelectedItem().toString());
		} catch (NumberFormatException ex) {
			cmbZoom.setSelectedItem(getPreferredZoomValue());
		}
		pnlViewer.setZoom(Integer
				.parseInt(cmbZoom.getSelectedItem().toString()));
		updateViewer();
	}

	@Override
	public void load() {
		try {
			pnlViewer.setImage(getObject().getBufferedImage());
			updateZoomValue(getPreferredZoomValue());
			setDirty(false);
		} catch (IOException ex) {
			// TODO report
			ex.printStackTrace();
		}
	}

	@Override
	public void save() {
		getObject().setFile(localFile);
		setDirty(false);
	}

	@Override
	public Image getObject() {
		return (Image) super.getObject();
	}

	private void updateViewer() {
		scrViewer.revalidate();
		RepaintManager.currentManager(this).addInvalidComponent(scrViewer);
		scrViewer.repaint();
	}

	@Override
	protected void labelUpdated() {
		String title = getObject().getLabel();
		if (dirty) {
			title = "*" + title;
		}
		tabbedPane.setTitleAt(tabbedPane.indexOfComponent(this), title);
		this.setName(title);
	}

	private void showImageChooser() {
		if (imageChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION
				&& imageChooser.getSelectedFile() != null) {
			localFile = imageChooser.getSelectedFile();
			try {
				pnlViewer.setImage(ImageIO.read(localFile));
				setDirty(true);
				updateViewer();
			} catch (IOException ex) {
				// TODO report
				ex.printStackTrace();
			}
			setDirty(true);
		}
	}

	@Override
	public String toString() {
		return getObject().getLabel();
	}
	
	/*
	 * Components
	 */
	private PnlImageViewer pnlViewer = new PnlImageViewer();
	private JPanel pnlToolbar = new JPanel();
	private JLabel lblZoom = new JLabel();
	private JComboBox cmbZoom = new JComboBox();
	private JLabel lblPercent = new JLabel();
	private Integer[] defaultZoomValues = new Integer[] { 50, 75, 100, 125,
			200, 400, 800 };
	private DefaultComboBoxModel modZoom = new DefaultComboBoxModel(
			defaultZoomValues);
	private JScrollPane scrViewer = new JScrollPane(pnlViewer);
	private JButton btnZoomIn = new JButton();
	private JButton btnZoomOut = new JButton();
	private JButton btnChange = new JButton();
	private JFileChooser imageChooser = new JFileChooser();
	private File localFile = null;

}
