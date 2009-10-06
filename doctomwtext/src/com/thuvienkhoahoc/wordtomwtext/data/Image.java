package com.thuvienkhoahoc.wordtomwtext.data;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.sourceforge.jwbf.contentRep.mw.SimpleFile;

public class Image extends SimpleFile {

	private Project project;
	private boolean markedForRemoval;
	private BufferedImage bufferedImage = null;
	/*
	 * Thay thế trường file của SimpleFile do nó k cho phép
	 * thay đổi giá trị của trường này.
	 * XXX sửa lại SimpleFile
	 */
	private File file;

	public Image(String label, String filename) {
		super(label, filename);
		file = super.getFile();
	}

	public Project getProject() {
		return project;
	}

	void setProject(Project project) {
		this.project = project;
	}

	public boolean isMarkedForRemoval() {
		return markedForRemoval;
	}

	public void setMarkedForRemoval(boolean markedForRemoval) {
		if (this.markedForRemoval != markedForRemoval) {
			boolean oldValue = this.markedForRemoval;
			this.markedForRemoval = markedForRemoval;
			if (project != null) {
				project.fireImagePropertyChanged(this, oldValue,
						markedForRemoval, "markedForRemoval");
			}
		}
	}

	public BufferedImage getBufferedImage() throws IOException {
		if (bufferedImage == null) {
			bufferedImage = ImageIO.read(getFile());
		}
		return bufferedImage;
	}

	@Override
	public void setLabel(String label) {
		String oldLabel = getLabel();
		super.setLabel(label);
		if (project != null) {
			project.fireImagePropertyChanged(this, oldLabel, label, "label");
		}
	}

	@Override
	public String toString() {
		return getLabel();
	}
	
	@Override
	public File getFile() {
		return file;
	}

	@Override
	public String getFilename() {
		return file.getPath();
	}
	
	public void setFile(File file) {
		this.file = file;
		bufferedImage = null;
	}
	
}
