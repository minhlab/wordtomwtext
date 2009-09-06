package com.thuvienkhoahoc.wordtomwtext;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import net.sourceforge.jwbf.bots.MediaWikiBot;

public class Application {

	private DlgLogin dlgLogin = new DlgLogin(null);
	private MediaWikiBot bot;
	private String username;
	private ArrayList<File> files = new ArrayList<File>();
	private ArrayList<Page> pages = new ArrayList<Page>();

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

	/*
	 * Accessors
	 */
	
	public List<File> getFiles() {
		return files;
	}
	
	public List<Page> getPages() {
		return pages;
	}
	
	private void run() {
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				// initialize
				if (!login()) {
					System.out.println("Can't login, application closed.");
					return;
				}

				// run main frame
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
