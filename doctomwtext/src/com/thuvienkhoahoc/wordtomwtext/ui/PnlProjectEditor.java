package com.thuvienkhoahoc.wordtomwtext.ui;

import java.awt.BorderLayout;
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
	}

	private void initComponents() {
		this.setLayout(new BorderLayout());

		treeProject.setBorder(BorderFactory.createEtchedBorder());
		treeProject.setModel(modProject);
		treeProject.setCellRenderer(new ProjectTreeCellRenderer());
		treeProject.setCellEditor(new ProjectTreeCellEditor(treeProject));
		treeProject.setEditable(true);
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
							miRemove.setSelected(((Page) lastComponent).isMarkedForRemoval());
						} else if (lastComponent instanceof Image) {
							articleBasedActionEnabled = true;
							miRemove.setSelected(((Image) lastComponent).isMarkedForRemoval());
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
		JScrollPane scrProject = new JScrollPane(treeProject);
		scrProject.setPreferredSize(new Dimension(280, 600));
		add(scrProject, BorderLayout.WEST);

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
		pnlMain.setPreferredSize(new Dimension(700, 600));
		pnlMain.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
		add(pnlMain, BorderLayout.CENTER);

		miSave.setText("Lưu");
		miSave.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				onSave();
			}
		});
		popupEditor.add(miSave);

		miDiscard.setText("Hủy bỏ");
		miDiscard.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				onDiscard();
			}
		});
		popupEditor.add(miDiscard);

		popupEditor.addSeparator();

		miClose.setText("Đóng");
		miClose.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				onClose();
			}
		});
		popupEditor.add(miClose);

		miCloseAll.setText("Đóng tất cả");
		miCloseAll.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				onCloseAll();
			}
		});
		popupEditor.add(miCloseAll);

		miCloseOthers.setText("Đóng các mục khác");
		miCloseOthers.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				onCloseOthers();
			}
		});
		popupEditor.add(miCloseOthers);

		miCreatePage.setText("Trang mới");
		miCreatePage.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				createPage();
			}
		});
		popupTree.add(miCreatePage);

		popupTree.addSeparator();

		miCreateSubpage.setText("Trang con mới");
		miCreateSubpage.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				createSubpage();
			}
		});
		popupTree.add(miCreateSubpage);

		miRename.setText("Đổi tên");
		miRename.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				renameSelectedAritcle();
			}
		});
		popupTree.add(miRename);

		miRemove.setText("Đánh dấu xóa");
		miRemove.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				toggleMarkedForRemoval();
			}
		});
		popupTree.add(miRemove);
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
			pnlMain.setSelectedIndex(pnlMain.getTabCount() - 1);
		}
	}

	@Override
	public boolean next() {
		return false;
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

	private void onSave() {
		PnlPageEditor pageEditor = (PnlPageEditor) pnlMain
				.getSelectedComponent();
		if (pageEditor != null) {
			pageEditor.save();
		}
	}

	private void onDiscard() {
		PnlPageEditor pageEditor = (PnlPageEditor) pnlMain
				.getSelectedComponent();
		if (pageEditor != null) {
			pageEditor.discard();
		}
	}

	private void onCloseOthers() {
		int selectedIndex = pnlMain.getSelectedIndex();
		for (int i = pnlMain.getTabCount() - 1; i >= 0; i--) {
			if (i != selectedIndex) {
				if (!closeEditorTab(i)) {
					break;
				}
			}
		}
	}

	private void onCloseAll() {
		for (int i = pnlMain.getTabCount() - 1; i >= 0; i--) {
			if (!closeEditorTab(i)) {
				break;
			}
		}
	}

	private void onClose() {
		closeEditorTab(pnlMain.getSelectedIndex());
	}

	private boolean closeEditorTab(int index) {
		if (((PnlEditor) pnlMain.getComponentAt(index)).isDirty()) {
			int choice = JOptionPane.showConfirmDialog(this,
					"Bạn có muốn lưu những thay đổi vừa thực hiện không?",
					"Bài viết chưa được lưu", JOptionPane.YES_NO_CANCEL_OPTION);
			if (choice == JOptionPane.CANCEL_OPTION) {
				return false;
			}
			if (choice == JOptionPane.YES_OPTION) {
				((PnlEditor) pnlMain.getComponentAt(index)).save();
			}
		}
		pnlMain.remove(index);
		return true;
	}

	/*
	 * Components
	 */
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
