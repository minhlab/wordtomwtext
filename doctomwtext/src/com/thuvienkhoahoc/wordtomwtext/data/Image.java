package com.thuvienkhoahoc.wordtomwtext.data;

import net.sourceforge.jwbf.contentRep.mw.SimpleFile;

public class Image extends SimpleFile {

	private Project project;
	private boolean markedForRemoval;

	public Image(String label, String filename) {
		super(label, filename);
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

}
