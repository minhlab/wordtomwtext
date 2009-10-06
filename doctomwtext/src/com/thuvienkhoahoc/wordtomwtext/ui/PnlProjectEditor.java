package com.thuvienkhoahoc.wordtomwtext.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import com.thuvienkhoahoc.wordtomwtext.data.Image;
import com.thuvienkhoahoc.wordtomwtext.data.Page;
import com.thuvienkhoahoc.wordtomwtext.data.Project;

@SuppressWarnings("serial")
public class PnlProjectEditor extends AbstractFunctionalPanel {

	private Project project = new Project();

	public PnlProjectEditor() {
		initComponents();
		handleEvents();
	}

	private void initComponents() {
		this.setLayout(new BorderLayout());

		pnlWrapper.setDividerLocation(200);
		this.add(pnlWrapper, BorderLayout.CENTER);

		treeProject.setBorder(BorderFactory.createEtchedBorder());
		treeProject.setModel(modProject);
		treeProject.setCellRenderer(new ProjectTreeCellRenderer());
		treeProject.setCellEditor(new ProjectTreeCellEditor(treeProject));
		treeProject.setEditable(true);
		JScrollPane scrProject = new JScrollPane(treeProject);
		scrProject.setPreferredSize(new Dimension(280, 600));
		pnlWrapper.setLeftComponent(scrProject);

		pnlMain.setPreferredSize(new Dimension(700, 600));
		pnlMain.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
		pnlWrapper.setRightComponent(pnlMain);

		miSave.setText("Lưu");
		popupEditor.add(miSave);

		miDiscard.setText("Hủy bỏ");
		popupEditor.add(miDiscard);

		popupEditor.addSeparator();

		miClose.setText("Đóng");
		popupEditor.add(miClose);

		miCloseAll.setText("Đóng tất cả");
		popupEditor.add(miCloseAll);

		miCloseOthers.setText("Đóng các mục khác");
		popupEditor.add(miCloseOthers);

		miCreatePage.setText("Trang mới");
		popupTree.add(miCreatePage);

		popupTree.addSeparator();

		miCreateSubpage.setText("Trang con mới");
		popupTree.add(miCreateSubpage);

		miRename.setText("Đổi tên");
		popupTree.add(miRename);

		miRemove.setText("Đánh dấu xóa");
		popupTree.add(miRemove);
	}

	private void handleEvents() {
		treeProject.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					openSelectedArticle();
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
				maybeShowPopup(e);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				maybeShowPopup(e);
			}

			private void maybeShowPopup(MouseEvent e) {
				// // chỉ bật popup nếu click thẳng vào đối tượng
				// TreePath closestPath = treeProject.getUI()
				// .getClosestPathForLocation(treeProject, e.getX(),
				// e.getY());
				// if (closestPath == null
				// || !treeProject.getUI().getPathBounds(treeProject,
				// closestPath).contains(e.getX(), e.getY())) {
				// return;
				// }
				if (e.isPopupTrigger()) {
					boolean articleBasedActionEnabled = false;
					boolean pageBasedActionEnabled = false;
					TreePath selectionPath = treeProject.getSelectionPath();
					if (selectionPath != null) {
						Object lastComponent = selectionPath
								.getLastPathComponent();
						if (lastComponent instanceof Page) {
							pageBasedActionEnabled = articleBasedActionEnabled = true;
							miRemove.setSelected(((Page) lastComponent)
									.isMarkedForRemoval());
						} else if (lastComponent instanceof Image) {
							articleBasedActionEnabled = true;
							miRemove.setSelected(((Image) lastComponent)
									.isMarkedForRemoval());
						}
					}
					miRename.setEnabled(articleBasedActionEnabled);
					miRemove.setEnabled(articleBasedActionEnabled);
					miCreateSubpage.setEnabled(pageBasedActionEnabled);
					popupTree.show(treeProject, e.getX(), e.getY());
				}
			}

		});
		treeProject.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					openSelectedArticle();
				} else if (e.getKeyCode() == KeyEvent.VK_DELETE) {
					toggleMarkedForRemoval();
				}
				super.keyPressed(e);
			}
		});

		pnlMain.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				maybeShowPopup(e);
			}

			public void mouseReleased(MouseEvent e) {
				maybeShowPopup(e);
			}

			private void maybeShowPopup(MouseEvent e) {
				// // chỉ bật popup nếu click thẳng vào đối tượng
				// int tabNumber = pnlMain.getUI().tabForCoordinate(pnlMain,
				// e.getX(), e.getY());
				// if (tabNumber < 0) {
				// return;
				// }
				if (e.isPopupTrigger()) {
					boolean dirty = ((PnlEditor) pnlMain.getSelectedComponent())
							.isDirty();
					miCloseOthers.setEnabled(pnlMain.getTabCount() > 1);
					miSave.setEnabled(dirty);
					miDiscard.setEnabled(dirty);
					popupEditor.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});

		miSave.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				saveSelectedEditor();
			}
		});

		miDiscard.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				discardSelectedEditor();
			}
		});
		miClose.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				closeSelectedEditor();
			}
		});
		miCloseAll.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				closeAllEditors();
			}
		});
		miCloseOthers.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				closeOtherEditors();
			}
		});
		miCreatePage.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				createPage();
			}
		});
		miCreateSubpage.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				createSubpage();
			}
		});
		miRename.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				renameSelectedAritcle();
			}
		});
		miRemove.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				toggleMarkedForRemoval();
			}
		});
	}

	@Override
	public void load(Object obj) {
		project = (Project) obj;
		modProject.setProject(project);
		// pre-open tabs for main pages only
		pnlMain.removeAll();
		for (Page page : project.getPages()) {
			openEditorTab(page);
		}
	}

	private void openEditorTab(Object obj) {
		for (int i = 0; i < pnlMain.getTabCount(); i++) {
			if (((PnlEditor) pnlMain.getComponentAt(i)).getObject() == obj) {
				pnlMain.setSelectedIndex(i);
				return;
			}
		}

		PnlEditor editor = null;
		String label = "";
		if (obj instanceof Page) {
			Page page = (Page) obj;
			editor = new PnlPageEditor(pnlMain, page);
			label = page.getLabel();
		} else if (obj instanceof Image) {
			Image image = (Image) obj;
			editor = new PnlImageEditor(pnlMain, image);
			label = image.getLabel();
		}
		if (editor != null) {
			pnlMain.addTab(label, editor);
			editor.load();
			pnlMain.setSelectedIndex(pnlMain.getTabCount() - 1);
		}
	}

	@Override
	public boolean next() {
		for (int i = 0; i < pnlMain.getTabCount(); i++) {
			PnlEditor editor = (PnlEditor) pnlMain.getComponentAt(i);
			if (!ensureSaved(editor)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public Project getResult() {
		return project;
	}

	/*
	 * Event handlers
	 */
	private void openSelectedArticle() {
		TreePath selectionPath = treeProject.getSelectionPath();
		if (selectionPath != null) {
			openEditorTab(selectionPath.getLastPathComponent());
		}
	}

	private void createPage() {
		String label = JOptionPane.showInputDialog(this,
				"Bạn hãy nhập tên bài viết (không chứa dấu \"/\")",
				"Tạo bài viết mới", JOptionPane.QUESTION_MESSAGE);
		label = label.replaceAll("/", "");
		project.addPage(new Page(label));
	}

	private void createSubpage() {
		TreePath selectionPath = treeProject.getSelectionPath();
		if (selectionPath != null) {
			Object selectedObject = selectionPath.getLastPathComponent();
			if (selectedObject instanceof Page) {
				Page basepage = (Page) selectedObject;
				String label = JOptionPane.showInputDialog(this,
						"Bạn hãy nhập tên bài viết (không chứa dấu \"/\")",
						"Tạo bài viết con mới", JOptionPane.QUESTION_MESSAGE);
				label = label.replaceAll("/", "");
				basepage.addPage(new Page(label));
			}
		}
	}

	private void toggleMarkedForRemoval() {
		TreePath selectionPath = treeProject.getSelectionPath();
		if (selectionPath != null) {
			Object selectedObject = selectionPath.getLastPathComponent();
			if (selectedObject instanceof Page) {
				Page page = (Page) selectedObject;
				page.setMarkedForRemoval(!page.isMarkedForRemoval());
			} else if (selectedObject instanceof Image) {
				Image image = (Image) selectedObject;
				image.setMarkedForRemoval(!image.isMarkedForRemoval());
			}
		}
	}

	private void renameSelectedAritcle() {
		treeProject.startEditingAtPath(treeProject.getSelectionPath());
	}

	private void saveSelectedEditor() {
		PnlEditor editor = (PnlEditor) pnlMain.getSelectedComponent();
		if (editor != null) {
			editor.save();
		}
	}

	private void discardSelectedEditor() {
		PnlEditor editor = (PnlEditor) pnlMain.getSelectedComponent();
		if (editor != null) {
			editor.load();
		}
	}

	private void closeOtherEditors() {
		int selectedIndex = pnlMain.getSelectedIndex();
		for (int i = pnlMain.getTabCount() - 1; i >= 0; i--) {
			if (i != selectedIndex) {
				if (!closeEditorTab(i)) {
					break;
				}
			}
		}
	}

	private void closeAllEditors() {
		for (int i = pnlMain.getTabCount() - 1; i >= 0; i--) {
			if (!closeEditorTab(i)) {
				break;
			}
		}
	}

	private void closeSelectedEditor() {
		closeEditorTab(pnlMain.getSelectedIndex());
	}

	private boolean closeEditorTab(int index) {
		PnlEditor editor = (PnlEditor) pnlMain.getComponentAt(index);
		if (!ensureSaved(editor)) {
			return false;
		}
		pnlMain.remove(index);
		return true;
	}

	private boolean ensureSaved(PnlEditor editor) {
		if (editor.isDirty()) {
			int choice = JOptionPane.showConfirmDialog(this,
					"Bạn có muốn lưu những thay đổi vừa thực hiện không?",
					"Bài viết \"" + editor.getName() + "\" chưa được lưu",
					JOptionPane.YES_NO_CANCEL_OPTION);
			if (choice == JOptionPane.CANCEL_OPTION) {
				return false;
			}
			if (choice == JOptionPane.YES_OPTION) {
				editor.save();
			}
		}
		return true;
	}

	/*
	 * Components
	 */
	private JSplitPane pnlWrapper = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
	private JTabbedPane pnlMain = new JTabbedPane();
	private JTree treeProject = new JTree();
	private ProjectTreeModel modProject = new ProjectTreeModel();
	private JPopupMenu popupEditor = new JPopupMenu();
	private JMenuItem miSave = new JMenuItem();
	private JMenuItem miDiscard = new JMenuItem();
	private JMenuItem miClose = new JMenuItem();
	private JMenuItem miCloseAll = new JMenuItem();
	private JMenuItem miCloseOthers = new JMenuItem();
	private JPopupMenu popupTree = new JPopupMenu();
	private JMenuItem miRename = new JMenuItem();
	private JCheckBoxMenuItem miRemove = new JCheckBoxMenuItem();
	private JMenuItem miCreateSubpage = new JMenuItem();
	private JMenuItem miCreatePage = new JMenuItem();

}
