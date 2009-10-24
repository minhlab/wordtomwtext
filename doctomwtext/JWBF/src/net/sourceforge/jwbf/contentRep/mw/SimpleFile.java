/*
 * Copyright 2007 Justus Bisser.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * Contributors:
 * 
 */
package net.sourceforge.jwbf.contentRep.mw;

import java.io.File;

/**
 * This is a simple content helper class that implements the
 * EditContentAccesable interface, plus setter methods.
 * The field Text from SimpleArticle can be used as a description for the file.
 * 
 * @author Justus Bisser
 * 
 */
public class SimpleFile extends SimpleArticle {

	private File file;
	
	/**
	 * 
	 * @param Label new filename
	 * @param Filename local filename
	 */
	public SimpleFile(final String Label, String Filename) {
		setText("");
		setLabel(Label);
		file = new File(Filename);
	}

	/**
	 * 
	 * @param Label new filename
	 * @param Filename local filename
	 */
	public SimpleFile(final String Label, File Filename) {
		setText("");
		setLabel(Label);
		file = Filename;
	}
	
	/**
	 * 
	 * @param Filename local filename
	 */
	public SimpleFile(File Filename) {
		setText("");
		setLabel(Filename.getName());
		file = Filename;
	}
	
	/**
	 * 
	 * @param Filename local filename
	 */
	public SimpleFile(String Filename) {
		setText("");
		file = new File(Filename);
		setLabel(file.getName());
	}

	public String getFilename() {
		return file.getPath();
	}

	public File getFile()
	{
		return this.file;
	}
	
	public void setFile(File file) {
		this.file = file;
	}
}
