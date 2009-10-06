package com.thuvienkhoahoc.wordtomwtext;

import java.net.MalformedURLException;

import javax.swing.SwingUtilities;

import net.sourceforge.jwbf.actions.mw.util.ActionException;
import net.sourceforge.jwbf.bots.MediaWikiBot;

import com.thuvienkhoahoc.wordtomwtext.ui.DlgLogin;
import com.thuvienkhoahoc.wordtomwtext.ui.FrmMain;

public class Application {

	private DlgLogin dlgLogin = new DlgLogin(null);
	private MediaWikiBot bot = null;
	private String username = "";
	private String sitename = "";
	private boolean logedin = false;

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
	
	public boolean isLogedin() {
		return logedin;
	}

	public void login(String site, String username, String password)
			throws ActionException, MalformedURLException {
		bot = new MediaWikiBot(site);
		bot.login(username, password);
		logedin = true;

		this.username = username;
		try {
			sitename = bot.getSiteinfo().getSitename();
		} catch (ActionException e) {
			sitename = "<không rõ>";
			e.printStackTrace();
		}
	}

	public void logout() {
		bot = null;
		username = sitename = "";
		logedin = false;
	}

	public boolean showLoginDialog() {
		dlgLogin.setVisible(true);
		return logedin;
	}

	public void run() {
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				if (!logedin) {
					if (!showLoginDialog()) {
						System.out.println("Can't login, application closed.");
						exit(0);
					}
				}
				new FrmMain().setVisible(true);
			}
		});
	}

	public void exit(int status) {
		System.exit(status);
	}

	private static Application instance = new Application();

	public static Application getInstance() {
		return instance;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		instance.run();
	}

}
