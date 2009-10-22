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

	/*
	 * Được gọi từ hàm Project.addPage hoặc Page.addPage, các thao tác 
	 * kiểm tra đã được thực hiện xong.
	 */
	void setProject(Project project) {
		this.project = project;
	}

	public Page getParent() {
		return parent;
	}

	void setParent(Page newParent) {
		if (parent == newParent) {
			return;
		}
		if (parent != null) {
			parent.removePage(this);
		}
		String shortLabel = getShortLabel();
		parent = newParent;
		setShortLabel(shortLabel);
	}

	public void addPage(Page newPage) throws DuplicatedTitleException {
		if (subpageList.contains(newPage)) {
			return;
		}
		if (newPage.hasChild()) {
			throw new IllegalArgumentException("Không được phép thêm trang có con.");
		}
		if (newPage.isSubpage() || newPage.getProject() != null) {
			throw new IllegalArgumentException("Remove from current project and parent first.");
		}
		
		project
				.checkPageTitle(getLabel() + "/" + newPage.getShortLabel(),
						null);
		if (subpageList.add(newPage)) {
			newPage.setParent(this);
			newPage.setProject(this.project);
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
		if (label == null || label.isEmpty()) {
			throw new IllegalArgumentException("Label must not be empty.");
		}
		if (isSubpage()) {
			setLabel(parent.getLabel() + "/" + label);
		} else {
			setLabel(label);
		}
	}
	
	public void setShortLabelAndRefactor(String newShortLabel) {
		String oldLabel = getLabel();
		setShortLabel(newShortLabel);
		project.refactorPageRenamed(oldLabel, getLabel());
	}

	@Override
	public void setLabel(String newLabel) {
		if (newLabel == null || newLabel.isEmpty()) {
			throw new IllegalArgumentException("Label must not be empty.");
		}
		if (isSubpage() && !newLabel.startsWith(parent.getLabel() + "/")) {
			throw new IllegalArgumentException(
					"Subpage label must start with parent label plus '/'. Try to use setShortLabel() instead.");
		}
		if (getLabel().equals(newLabel)) {
			return;
		}
		if (project != null) {
			project.checkPageTitle(newLabel, this);
		}
		String oldLabel = getLabel();
		super.setLabel(newLabel);
		if (project != null) {
			project.firePagePropertyChanged(this, oldLabel, newLabel, "label");
		}
	}
	
	public void setLabelAndRefactor(String newLabel) {
		String oldLabel = getLabel();
		setLabel(newLabel);
		project.refactorPageRenamed(oldLabel, newLabel);
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
