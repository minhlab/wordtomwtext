package com.thuvienkhoahoc.wordtomwtext;

import javax.swing.SwingUtilities;

import net.sourceforge.jwbf.actions.mw.util.ActionException;
import net.sourceforge.jwbf.bots.MediaWikiBot;

import com.thuvienkhoahoc.wordtomwtext.ui.DlgLogin;
import com.thuvienkhoahoc.wordtomwtext.ui.FrmMain;

public class Application {

	private DlgLogin dlgLogin = new DlgLogin(null);
	private MediaWikiBot bot;
	private String username;
	private String sitename;

	private Application() {
	}

	public String getUsername() {
		return username;
	}
	
	public String getSitename() {
		return sitename;
	}
	
	public MediaWikiBot getBot() {
		return bot;
	}

	public boolean login() {
		dlgLogin.setVisible(true);
		username = dlgLogin.getUsername();
		bot = dlgLogin.getBot();
		if (bot != null) {
			try {
				sitename = bot.getSiteinfo().getSitename();
			} catch (ActionException e) {
				sitename = "<không rõ>";
				e.printStackTrace();
			}
		}
		return bot != null;
	}

	/*
	 * Accessors
	 */
	
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
