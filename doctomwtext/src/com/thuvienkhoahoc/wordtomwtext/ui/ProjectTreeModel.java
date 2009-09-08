package com.thuvienkhoahoc.wordtomwtext.ui;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import com.thuvienkhoahoc.wordtomwtext.data.Image;
import com.thuvienkhoahoc.wordtomwtext.data.Page;
import com.thuvienkhoahoc.wordtomwtext.data.Project;

public class ProjectTreeModel implements TreeModel {

	private static final String PLACEHOLDER_PROJECT = "Dự án";
	private static final String PLACEHOLDER_PAGES = "Bài viết";
	private static final String PLACEHOLDER_IMAGES = "Hình ảnh";

	private EventListenerList listenerList = new EventListenerList();
	private Project project = new Project();

	public ProjectTreeModel() {
		super();
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
		fireTreeStructureChanged(this, null);
	}

	public void imageChanged(Image image) {
		// TODO implement this
	}

	public void pageChanged(Page page) {
		// TODO implement this
	}

	/*
	 * Implement TreeModel
	 * 
	 * @seejavax.swing.tree.TreeModel#addTreeModelListener(javax.swing.event.
	 * TreeModelListener)
	 */

	public Object getChild(Object parent, int index) {
		if (parent == PLACEHOLDER_PROJECT) {
			switch (index) {
			case 0:
				return PLACEHOLDER_PAGES;
			case 1:
				return PLACEHOLDER_IMAGES;
			}
		}
		if (parent == PLACEHOLDER_PAGES) {
			return project.getPages().get(index);
		}
		if (parent instanceof Page) {
			Page page = (Page) parent;
			return page.getChildren().get(index);
		}
		if (parent == PLACEHOLDER_IMAGES) {
			return project.getImages().get(index);
		}
		return null;
	}

	public int getChildCount(Object parent) {
		if (parent == PLACEHOLDER_PROJECT) {
			return 2;
		}
		if (parent == PLACEHOLDER_PAGES) {
			return project.getPages().size();
		}
		if (parent instanceof Page) {
			Page page = (Page) parent;
			return page.getChildren().size();
		}
		if (parent == PLACEHOLDER_IMAGES) {
			return project.getImages().size();
		}
		return 0;
	}

	public int getIndexOfChild(Object parent, Object child) {
		if (parent == PLACEHOLDER_PROJECT) {
			if (child == PLACEHOLDER_PAGES) {
				return 0;
			}
			if (child == PLACEHOLDER_IMAGES) {
				return 1;
			}
		}
		if (parent == PLACEHOLDER_PAGES) {
			return project.getPages().indexOf(child);
		}
		if (parent instanceof Page) {
			Page page = (Page) parent;
			return page.getChildren().indexOf(child);
		}
		if (parent == PLACEHOLDER_IMAGES) {
			return project.getImages().indexOf(child);
		}
		return 0;
	}

	public Object getRoot() {
		return PLACEHOLDER_PROJECT;
	}

	public boolean isLeaf(Object node) {
		if (node == PLACEHOLDER_PROJECT || node == PLACEHOLDER_PAGES
				|| node == PLACEHOLDER_IMAGES) {
			return false;
		}
		if (node instanceof Page) {
			Page page = (Page) node;
			return page.getChildren().isEmpty();
		}
		return true;
	}

	public void valueForPathChanged(TreePath path, Object newValue) {
		// TODO Auto-generated method stub
	}

	/*
	 * Events
	 */

	/**
	 * Adds a listener for the TreeModelEvent posted after the tree changes.
	 * 
	 * @see #removeTreeModelListener
	 * @param l
	 *            the listener to add
	 */
	public void addTreeModelListener(TreeModelListener l) {
		listenerList.add(TreeModelListener.class, l);
	}

	/**
	 * Removes a listener previously added with <B>addTreeModelListener()</B>.
	 * 
	 * @see #addTreeModelListener
	 * @param l
	 *            the listener to remove
	 */
	public void removeTreeModelListener(TreeModelListener l) {
		listenerList.remove(TreeModelListener.class, l);
	}

	/**
	 * Returns an array of all the tree model listeners registered on this
	 * model.
	 * 
	 * @return all of this model's <code>TreeModelListener</code>s or an empty
	 *         array if no tree model listeners are currently registered
	 * 
	 * @see #addTreeModelListener
	 * @see #removeTreeModelListener
	 * 
	 * @since 1.4
	 */
	public TreeModelListener[] getTreeModelListeners() {
		return (TreeModelListener[]) listenerList
				.getListeners(TreeModelListener.class);
	}

	/**
	 * Notifies all listeners that have registered interest for notification on
	 * this event type. The event instance is lazily created using the
	 * parameters passed into the fire method.
	 * 
	 * @param source
	 *            the node being changed
	 * @param path
	 *            the path to the root node
	 * @param childIndices
	 *            the indices of the changed elements
	 * @param children
	 *            the changed elements
	 * @see EventListenerList
	 */
	protected void fireTreeNodesChanged(Object source, Object[] path,
			int[] childIndices, Object[] children) {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		TreeModelEvent e = null;
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == TreeModelListener.class) {
				// Lazily create the event:
				if (e == null)
					e = new TreeModelEvent(source, path, childIndices, children);
				((TreeModelListener) listeners[i + 1]).treeNodesChanged(e);
			}
		}
	}

	/**
	 * Notifies all listeners that have registered interest for notification on
	 * this event type. The event instance is lazily created using the
	 * parameters passed into the fire method.
	 * 
	 * @param source
	 *            the node where new elements are being inserted
	 * @param path
	 *            the path to the root node
	 * @param childIndices
	 *            the indices of the new elements
	 * @param children
	 *            the new elements
	 * @see EventListenerList
	 */
	protected void fireTreeNodesInserted(Object source, Object[] path,
			int[] childIndices, Object[] children) {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		TreeModelEvent e = null;
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == TreeModelListener.class) {
				// Lazily create the event:
				if (e == null)
					e = new TreeModelEvent(source, path, childIndices, children);
				((TreeModelListener) listeners[i + 1]).treeNodesInserted(e);
			}
		}
	}

	/**
	 * Notifies all listeners that have registered interest for notification on
	 * this event type. The event instance is lazily created using the
	 * parameters passed into the fire method.
	 * 
	 * @param source
	 *            the node where elements are being removed
	 * @param path
	 *            the path to the root node
	 * @param childIndices
	 *            the indices of the removed elements
	 * @param children
	 *            the removed elements
	 * @see EventListenerList
	 */
	protected void fireTreeNodesRemoved(Object source, Object[] path,
			int[] childIndices, Object[] children) {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		TreeModelEvent e = null;
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == TreeModelListener.class) {
				// Lazily create the event:
				if (e == null)
					e = new TreeModelEvent(source, path, childIndices, children);
				((TreeModelListener) listeners[i + 1]).treeNodesRemoved(e);
			}
		}
	}

	/**
	 * Notifies all listeners that have registered interest for notification on
	 * this event type. The event instance is lazily created using the
	 * parameters passed into the fire method.
	 * 
	 * @param source
	 *            the node where the tree model has changed
	 * @param path
	 *            the path to the root node
	 * @param childIndices
	 *            the indices of the affected elements
	 * @param children
	 *            the affected elements
	 * @see EventListenerList
	 */
	protected void fireTreeStructureChanged(Object source, Object[] path,
			int[] childIndices, Object[] children) {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		TreeModelEvent e = null;
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == TreeModelListener.class) {
				// Lazily create the event:
				if (e == null)
					e = new TreeModelEvent(source, path, childIndices, children);
				((TreeModelListener) listeners[i + 1]).treeStructureChanged(e);
			}
		}
	}

	/*
	 * Notifies all listeners that have registered interest for notification on
	 * this event type. The event instance is lazily created using the
	 * parameters passed into the fire method.
	 * 
	 * @param source the node where the tree model has changed
	 * 
	 * @param path the path to the root node
	 * 
	 * @see EventListenerList
	 */
	private void fireTreeStructureChanged(Object source, TreePath path) {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		TreeModelEvent e = null;
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == TreeModelListener.class) {
				// Lazily create the event:
				if (e == null)
					e = new TreeModelEvent(source, path);
				((TreeModelListener) listeners[i + 1]).treeStructureChanged(e);
			}
		}
	}

}
