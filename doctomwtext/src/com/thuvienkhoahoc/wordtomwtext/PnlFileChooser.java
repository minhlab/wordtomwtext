package com.thuvienkhoahoc.wordtomwtext;

import java.awt.BorderLayout;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

@SuppressWarnings("serial")
public class PnlFileChooser extends AbstractFunctionalPanel {

	public PnlFileChooser() {
		setLayout(layout);
		
		realChooser.setFileFilter(new FileFilter() {
			
			@Override
			public String getDescription() {
				return "Tài liệu Microsoft Word 97/2000";
			}
			
			@Override
			public boolean accept(File f) {
				if (f.isDirectory()) {
					return true;
				}
				return f.getName().endsWith(".doc");
			}
		});
		realChooser.setControlButtonsAreShown(false);
		add(realChooser, BorderLayout.CENTER);
	}
	
	@Override
	public void load() {
		// nothing to do
	}

	@Override
	public boolean work() {
		File file = realChooser.getSelectedFile();
		if (file == null) {
			return false;
		}
		Application.getInstance().setWordFile(file);
		return true;
	}

	BorderLayout layout = new BorderLayout();
	JFileChooser realChooser = new JFileChooser();
}
