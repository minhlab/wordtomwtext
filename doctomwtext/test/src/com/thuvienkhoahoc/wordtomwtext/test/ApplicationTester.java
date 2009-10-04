package com.thuvienkhoahoc.wordtomwtext.test;

import java.net.MalformedURLException;

import net.sourceforge.jwbf.actions.mw.util.ActionException;

import com.thuvienkhoahoc.wordtomwtext.Application;

public class ApplicationTester {

	/**
	 * @param args
	 * @throws MalformedURLException
	 * @throws ActionException
	 */
	public static void main(String[] args) throws ActionException,
			MalformedURLException {
		Application.getInstance().login("http://thuvienkhoahoc.com/tusach/",
				"Tester", "1234a");
		Application.getInstance().run();
	}

}
