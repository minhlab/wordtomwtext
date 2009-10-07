package com.thuvienkhoahoc.wordtomwtext.test;

import java.awt.BorderLayout;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JFrame;

import net.sourceforge.jwbf.actions.mw.util.ActionException;

import com.thuvienkhoahoc.wordtomwtext.Application;
import com.thuvienkhoahoc.wordtomwtext.data.Image;
import com.thuvienkhoahoc.wordtomwtext.data.Page;
import com.thuvienkhoahoc.wordtomwtext.data.Project;
import com.thuvienkhoahoc.wordtomwtext.ui.PnlFinished;

@SuppressWarnings("serial")
public class FinishedPanelTester extends JFrame {

	/**
	 * @param args
	 * @throws IOException
	 * @throws ActionException
	 */
	public static void main(String[] args) throws IOException, ActionException {
		// prepare data
		Application.getInstance().login("http://thuvienkhoahoc.com/w14/",
				"Tester2", "1234a");

		Project project = new Project();
		long id = System.currentTimeMillis();

		Page page = new Page("Thử nghiệm" + id);
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

		Page page2 = new Page("Đơn giản" + id);
		page2.setText("abc");
		page.addPage(page2);

		Image image = new Image("picture-0" + id + ".png",
				"test/data/picture-0.png");
		project.addImage(image);

		// initialize frame
		JFrame frame = new JFrame();
		frame.setTitle("Project editor tester");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		PnlFinished panel = new PnlFinished();
		panel.load(project);
		frame.getContentPane().add(panel, BorderLayout.CENTER);

		frame.pack();
		frame.setVisible(true);
	}

}
