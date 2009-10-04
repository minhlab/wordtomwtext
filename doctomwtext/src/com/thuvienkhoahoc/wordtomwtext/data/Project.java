package com.thuvienkhoahoc.wordtomwtext.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Project {

	private List<ProjectListener> listenerList = new ArrayList<ProjectListener>();
	private List<Page> pageList = new ArrayList<Page>();
	private List<Page> immutablePageList = Collections
			.unmodifiableList(pageList);
	private List<Image> imageList = new ArrayList<Image>();
	private List<Image> immutableImageList = Collections
			.unmodifiableList(imageList);

	/**
	 * Return immutable view of page list. Don't modify it.
	 * 
	 * @return immutable view of page list
	 */
	public List<Page> getPages() {
		return immutablePageList;
	}

	/**
	 * Return immutable view of image list. Don't modify it.
	 * 
	 * @return immutable view of image list
	 */
	public List<Image> getImages() {
		return immutableImageList;
	}

	/**
	 * @param page
	 * @return
	 * @see java.util.List#add(java.lang.Object)
	 */
	public boolean addPage(Page page) {
		if (pageList.contains(page)) {
			return false;
		}
		if (page.getProject() != null) {
			page.getProject().removePage(page);
		}
		boolean ret = pageList.add(page);
		if (ret) {
			page.setProject(this);
			firePageAdded(page, pageList.indexOf(page));
		}
		return ret;
	}

	/**
	 * @param image
	 * @return
	 * @see java.util.List#add(java.lang.Object)
	 */
	public boolean addImage(Image image) {
		boolean ret = imageList.add(image);
		if (ret) {
			image.setProject(this);
			if (listenerList.size() > 0) {
				ProjectEvent event = new ProjectEvent(this, image, imageList
						.indexOf(image));
				for (ProjectListener listener : listenerList) {
					listener.imageAdded(event);
				}
			}
		}
		return ret;
	}

	/**
	 * @param page
	 * @return
	 * @see java.util.List#remove(java.lang.Object)
	 */
	public boolean removePage(Page page) {
		boolean ret = false;
		int index = -1;
		if (page.getParent() == null) {
			index = pageList.indexOf(page);
			ret = pageList.remove(page);
		} else {
			index = page.getParent().getChildren().indexOf(page);
			ret = page.getParent().removePage(page);
		}
		if (ret) {
			page.setProject(null);
			if (listenerList.size() > 0) {
				ProjectEvent event = new ProjectEvent(this, page, index);
				for (ProjectListener listener : listenerList) {
					listener.pageRemoved(event);
				}
			}
		}
		return ret;
	}

	/**
	 * @param image
	 * @return
	 * @see java.util.List#remove(java.lang.Object)
	 */
	public boolean removeImage(Image image) {
		int index = imageList.indexOf(image);
		boolean ret = imageList.remove(image);
		if (ret && listenerList.size() > 0) {
			ProjectEvent event = new ProjectEvent(this, image, index);
			for (ProjectListener listener : listenerList) {
				listener.imageRemoved(event);
			}
		}
		return ret;
	}

	public void importData(Project anotherProject) {
		pageList.addAll(anotherProject.pageList);
		imageList.addAll(anotherProject.imageList);
	}

	/**
	 * @param e
	 * @return
	 * @see java.util.List#add(java.lang.Object)
	 */
	public boolean addProjectListener(ProjectListener e) {
		return listenerList.add(e);
	}

	/**
	 * @param o
	 * @return
	 * @see java.util.List#remove(java.lang.Object)
	 */
	public boolean removeProjectListener(ProjectListener o) {
		return listenerList.remove(o);
	}

	void firePagePropertyChanged(Page page, Object oldValue, Object newValue,
			String propertyName) {
		if (listenerList.size() > 0) {
			ProjectEvent evt = new ProjectEvent(this, page, oldValue, newValue,
					propertyName);
			for (ProjectListener listener : listenerList) {
				listener.pagePropertyChanged(evt);
			}
		}
	}

	void fireImagePropertyChanged(Image image, Object oldValue,
			Object newValue, String propertyName) {
		if (listenerList.size() > 0) {
			ProjectEvent evt = new ProjectEvent(this, image, oldValue,
					newValue, propertyName);
			for (ProjectListener listener : listenerList) {
				listener.imagePropertyChanged(evt);
			}
		}
	}

	void firePageAdded(Page page, int index) {
		if (listenerList.size() > 0) {
			ProjectEvent event = new ProjectEvent(this, page, index);
			for (ProjectListener listener : listenerList) {
				listener.pageAdded(event);
			}
		}
	}

}