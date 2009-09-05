package com.thuvienkhoahoc.wordtomwtext;

import java.io.File;

import javax.swing.SwingUtilities;

import net.sourceforge.jwbf.bots.MediaWikiBot;

public class Application {

	private DlgLogin dlgLogin = new DlgLogin(null);
	private MediaWikiBot bot;
	private String username;
	private File wordFile;

	private Application() {
	}

	public String getUsername() {
		return username;
	}
	
	public MediaWikiBot getBot() {
		return bot;
	}

	public boolean login() {
		dlgLogin.setVisible(true);
		username = dlgLogin.getUsername();
		bot = dlgLogin.getBot();
		return bot != null;
	}

	public File getWordFile() {
		return wordFile;
	}
	
	public void setWordFile(File wordFile) {
		this.wordFile = wordFile;
	}
	
	private void run() {
		// initialize
		if (!login()) {
			System.out.println("Can't login, application closed.");
			return;
		}

		// run main frame
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				new FrmMain().setVisible(true);
			}
		});
	}

	private static Application instance;

	public static Application getInstance() {
		return instance;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		(instance = new Application()).run();
	}

}
