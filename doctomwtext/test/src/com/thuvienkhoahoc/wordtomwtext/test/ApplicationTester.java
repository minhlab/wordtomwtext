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
	public static void main(String[] args) throws Exception {
		Application.getInstance().login("http://thuvienkhoahoc.com/w14/",
				"Tester2", "1234a");
		Application.getInstance().run();
	}

}