package com.thuvienkhoahoc.wordtomwtext.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sourceforge.jwbf.contentRep.mw.SimpleArticle;

public class Page extends SimpleArticle {

	private Project project = null;
	private Page parent = null;
	private List<Page> subpageList = new ArrayList<Page>();
	private List<Page> immutableSubageList = null;
	private boolean markedForRemoval;

	public Page(String label) {
		super();
		setLabel(label);
	}

	public Project getProject() {
		return project;
	}

	void setProject(Project project) {
		this.project = project;
	}

	public Page getParent() {
		return parent;
	}

	void setParent(Page parent) {
		this.parent = parent;
	}

	public void addPage(Page newPage) {
		if (newPage.isSubpage()) {
			newPage.parent.removePage(newPage);
		}
		if (subpageList.add(newPage)) {
			newPage.setParent(this);
			if (project != null) {
				project.firePageAdded(newPage, subpageList.indexOf(newPage));
			}
		}
	}

	public boolean removePage(Page page) {
		boolean ret = subpageList.remove(page);
		if (ret) {
			page.setParent(null);
		}
		return ret;
	}

	public List<Page> getChildren() {
		if (immutableSubageList == null) {
			immutableSubageList = Collections.unmodifiableList(subpageList);
		}
		return immutableSubageList;
	}

	public boolean isSubpage() {
		return parent != null;
	}

	public boolean hasChild() {
		return subpageList.size() > 0;
	}

	public String getShortLabel() {
		if (isSubpage()) {
			return getLabel().substring(parent.getLabel().length());
		}
		return getLabel();
	}

	public void setShortLabel(String label) {
		if (isSubpage()) {
			setLabel(parent.getLabel() + "/" + label);
		} else {
			setLabel(label);
		}
	}

	@Override
	public void setLabel(String label) {
		if (isSubpage() && !label.startsWith(parent.getLabel() + "/")) {
			throw new IllegalArgumentException(
					"Subpage label must start with parent label plus '/'. Try to use setShortLabel() instead.");
		}
		if (!this.getLabel().equals(label)) {
			String oldLabel = getLabel();
			super.setLabel(label);
			if (project != null) {
				project.firePagePropertyChanged(this, oldLabel, label, "label");
			}
		}
	}

	public void setMarkedForRemoval(boolean markedForRemoval) {
		if (this.markedForRemoval != markedForRemoval) {
			boolean oldValue = this.markedForRemoval;
			this.markedForRemoval = markedForRemoval;
			if (project != null) {
				project.firePagePropertyChanged(this, oldValue,
						markedForRemoval, "markedForRemoval");
			}
		}
	}

	public boolean isMarkedForRemoval() {
		return markedForRemoval;
	}

	@Override
	public String toString() {
		return getLabel();
	}

}
