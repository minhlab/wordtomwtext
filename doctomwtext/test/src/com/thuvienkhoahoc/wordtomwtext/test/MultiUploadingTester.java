package com.thuvienkhoahoc.wordtomwtext.test;

import java.io.FileInputStream;
import java.io.FileReader;
import java.util.Properties;

import net.sourceforge.jwbf.bots.MediaWikiBot;

import com.thuvienkhoahoc.wordtomwtext.data.Page;

public class MultiUploadingTester {

	public static void main(String[] args) throws Exception {

		final Page page = new Page("Thử nghiệm1");
		FileReader input = new FileReader("test/data/mwtext.txt");
		char[] buffer = new char[1024];
		int numRead = 0;
		StringBuffer sb = new StringBuffer();
		while ((numRead = input.read(buffer)) > 0) {
			sb.append(buffer, 0, numRead);
		}
		input.close();
		page.setText(sb.toString());

		final Page page2 = new Page("Đơn giản");
		page2.setText("abc");

		Properties prop = new Properties();
		prop.load(new FileInputStream("test/config/testconfig.properties"));
		final MediaWikiBot bot = new MediaWikiBot(prop.getProperty("site"));
		bot.login(prop.getProperty("user"), prop.getProperty("pass"));

		new Thread() {
			public void run() {
				try {
					bot.writeContent(page);
				} catch (Exception e) {
					e.printStackTrace();
				}
			};
		}.start();
		new Thread() {
			public void run() {
				try {
					bot.writeContent(page2);
				} catch (Exception e) {
					e.printStackTrace();
				}
			};
		}.start();

	}

}
