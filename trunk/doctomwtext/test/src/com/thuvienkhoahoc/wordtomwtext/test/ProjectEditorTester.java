package com.thuvienkhoahoc.wordtomwtext.test;

import java.awt.BorderLayout;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JFrame;

import com.thuvienkhoahoc.wordtomwtext.data.Image;
import com.thuvienkhoahoc.wordtomwtext.data.Page;
import com.thuvienkhoahoc.wordtomwtext.data.Project;
import com.thuvienkhoahoc.wordtomwtext.ui.PnlProjectEditor;

public class ProjectEditorTester {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// prepare data
		Project project = new Project();
		
		Page page = new Page("Thử nghiệm");
		FileReader input = new FileReader("test/data/mwtext.txt");
		char[] buffer = new char[1024];
		int numRead = 0;
		StringBuffer sb = new StringBuffer();
		while ((numRead = input.read(buffer)) > 0) {
			sb.append(buffer, 0, numRead);
		}
		input.close();
		page.setText(sb.toString());
		project.addPage(page);
		
		Page page2 = new Page("Đơn giản");
		page2.setText("abc");
		project.addPage(page2);
		
		Image image = new Image("picture-0", "test/data/picture-0.png");
		project.addImage(image);
		
		// initialize frame
		JFrame frame = new JFrame();
		frame.setTitle("Project editor tester");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		PnlProjectEditor projEditor = new PnlProjectEditor();
		projEditor.load(project);
		frame.getContentPane().add(projEditor, BorderLayout.CENTER);
		
		frame.pack();
		frame.setVisible(true);
	}

}
