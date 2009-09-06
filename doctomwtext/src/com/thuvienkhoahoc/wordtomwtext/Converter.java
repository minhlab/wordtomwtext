package com.thuvienkhoahoc.wordtomwtext;

import java.io.File;

public class Converter {

	private static int identity = 0;
	
	public Page convert(File wordFile) {
		return new Page("test" + (++identity), "test");
	}
	
}
