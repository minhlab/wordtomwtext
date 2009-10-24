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

	public Image(String label, String filename) {
		super(label, filename);
	}

	public Project getProject() {
		return project;
	}

	/*
	 * Được gọi từ hàm Project.addImage, các thao tác kiểm tra đã
	 * thực hiện xong.
	 */
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
	public void setLabel(String newLabel) {
		if (newLabel.equals(getLabel())) {
			return;
		}
		if (project != null) {
			project.checkImageTitle(newLabel, this);
		}
		String oldLabel = getLabel();
		super.setLabel(newLabel);
		if (project != null) {
			project.fireImagePropertyChanged(this, oldLabel, newLabel, "label");
		}
	}
	
	public void setLabelAndRefactor(String newLabel) {
		String oldLabel = getLabel();
		setLabel(newLabel);
		project.refactorImageRenamed(oldLabel, newLabel);
	}

	@Override
	public String toString() {
		return getLabel();
	}
	
	public void setFile(File file) {
		super.setFile(file);
		bufferedImage = null;
	}
	
}
