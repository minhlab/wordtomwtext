package com.thuvienkhoahoc.wordtomwtext.test;

import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.util.Properties;

import net.sourceforge.jwbf.actions.mw.util.ActionException;

import com.thuvienkhoahoc.wordtomwtext.Application;

public class ApplicationTester {

	/**
	 * @param args
	 * @throws MalformedURLException
	 * @throws ActionException
	 */
	public static void main(String[] args) throws Exception {
		Properties prop = new Properties();
		prop.load(new FileInputStream(
				"test/config/testconfig.properties"));
		Application.getInstance().login(
				prop.getProperty("site"), 
				prop.getProperty("user"), 
				prop.getProperty("pass"));
		Application.getInstance().run();
	}

}