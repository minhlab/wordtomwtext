package com.thuvienkhoahoc.wordtomwtext.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.thuvienkhoahoc.wordtomwtext.Application;

@SuppressWarnings("serial")
public class FrmMain extends JFrame {

	public FrmMain() {
		super();
		initComponents();
		handleEvents();
		setSelectedIndex(0, false, null);
		updateUsernameField();
	}

	private void initComponents() {
		this.setTitle("Wordtomwtext - By VLOS");
		// this.setLocationRelativeTo(null);
		this.setLocationByPlatform(true);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		final String[] titles = new String[] { "Chọn tài liệu Word > ",
				"Chỉnh sửa nội dung > ", "Tải lên > ", "Kết thúc" };
		final AbstractFunctionalPanel[] panels = new AbstractFunctionalPanel[] {
				new PnlFileChooser(), new PnlProjectEditor(),
				new PnlUploader(), new PnlFinished() };

		pnlTabRun.setLayout(layoutTabRun);
		tabLabels = new JLabel[titles.length];
		for (int i = 0; i < titles.length; i++) {
			tabLabels[i] = new JLabel(titles[i]);
			tabLabels[i].setFont(DESELECTED_FONT);
			tabLabels[i].setForeground(DESELECTED_COLOR);
			pnlTabRun.add(tabLabels[i]);
		}

		pnlMainWrapper.setLayout(layoutMainWrapper);
		tabPanels = panels;
		for (int i = 0; i < titles.length; i++) {
			pnlMainWrapper.add(panels[i], titles[i]);
		}

		pnlToolbar.setLayout(new BoxLayout(pnlToolbar, BoxLayout.LINE_AXIS));

		pnlToolbar.add(pnlTabRun);

		pnlToolbar.add(Box.createHorizontalGlue());

		pnlToolbar.add(lblUsername);

		lblSignIn.setText(" (đăng nhập lại)");
		lblSignIn.setForeground(Color.BLUE);
		lblSignIn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		pnlToolbar.add(lblSignIn);

		getContentPane().add(pnlToolbar, BorderLayout.NORTH);

		getContentPane().add(pnlMainWrapper, BorderLayout.CENTER);

		// init button panel
		pnlButton.setLayout(layoutButton);
		pnlButton.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

		btnBack.setText("Quay lại");
		btnBack.setIcon(new ImageIcon(this.getClass()
				.getResource("../images/go-back.png")));
		pnlButton.add(btnBack);

		btnNext.setText("Tiếp tục");
		btnNext.setIcon(new ImageIcon(this.getClass()
				.getResource("../images/go-next.png")));
		pnlButton.add(btnNext);

		btnFirst.setText("Về đầu");
		btnFirst.setIcon(new ImageIcon(this.getClass()
				.getResource("../images/go-first.png")));
		pnlButton.add(btnFirst);

		btnExit.setText("Thoát");
		btnExit.setIcon(new ImageIcon(this.getClass()
				.getResource("../images/exit.png")));
		pnlButton.add(btnExit);

		getContentPane().add(pnlButton, BorderLayout.SOUTH);

		pack();
	}

	private void handleEvents() {
		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				quit();
			}
		});
		lblSignIn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					relogin();
				}
			}
		});
		btnBack.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				goBack();
			}
		});
		btnNext.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				goNext();
			}
		});
		btnFirst.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				goFirst();
			}
		});
		btnExit.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				quit();
			}
		});
		PropertyChangeListener tabPropertyChangeListener = new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getSource() == getSelectedTab()) {
					if ("state".equals(evt.getPropertyName())) {
						updateButtonState();
					}
				}
			}
		};
		for (int i = 0; i < tabPanels.length; i++) {
			tabPanels[i].addPropertyChangeListener(tabPropertyChangeListener);
		}
	}

	protected void quit() {
		if (getSelectedTab().canClose()) {
			Application.getInstance().exit(0);
		}
	}

	private void goNext() {
		if (getSelectedTab().canNext() && getSelectedTab().next()) {
			setSelectedIndex(selectedIndex + 1, false, getSelectedTab()
					.getResult());
		}
	}

	private void goBack() {
		if (getSelectedTab().canBack()) {
			setSelectedIndex(selectedIndex - 1, true, null);
		}
	}

	private void goFirst() {
		if (getSelectedTab().canBack()) {
			setSelectedIndex(0, true, null);
		}
	}

	private AbstractFunctionalPanel getSelectedTab() {
		return tabPanels[selectedIndex];
	}

	private void setSelectedIndex(int newIndex, boolean surpressLoad,
			Object data) {
		tabLabels[selectedIndex].setForeground(DESELECTED_COLOR);
		tabLabels[selectedIndex].setFont(DESELECTED_FONT);
		tabLabels[newIndex].setForeground(SELECTED_COLOR);
		tabLabels[newIndex].setFont(SELECTED_FONT);
		this.selectedIndex = newIndex;
		
		if (!surpressLoad) {
			tabPanels[newIndex].load(data);
		}
		layoutMainWrapper.show(pnlMainWrapper, tabLabels[newIndex]
				.getText());
		updateButtonState();
	}

	protected void relogin() {
		if (!getSelectedTab().canClose()) {
			return;
		}
		setVisible(false);
		Application.getInstance().logout();
		if (!Application.getInstance().showLoginDialog()) {
			Application.getInstance().exit(0);
		}
		updateUsernameField();
		setVisible(true);
	}

	private void updateButtonState() {
		btnBack.setEnabled(getSelectedTab().canBack());
		btnNext.setEnabled(getSelectedTab().canNext());
		btnFirst.setEnabled(selectedIndex == tabPanels.length-1);
	}

	private void updateUsernameField() {
		lblUsername.setText(Application.getInstance().getUsername() + " trên "
				+ Application.getInstance().getSitename());
		lblUsername.setToolTipText(Application.getInstance().getSiteurl());
	}

	// GUI items
	private JPanel pnlToolbar = new JPanel();
	private JLabel lblUsername = new JLabel();
	private JLabel lblSignIn = new JLabel();

	private JPanel pnlTabRun = new JPanel();
	private LayoutManager layoutTabRun = new FlowLayout(FlowLayout.LEFT);
	private JPanel pnlMainWrapper = new JPanel();
	private CardLayout layoutMainWrapper = new CardLayout();
	private JLabel[] tabLabels;
	private AbstractFunctionalPanel[] tabPanels;
	private int selectedIndex;

	private JPanel pnlButton = new JPanel();
	private FlowLayout layoutButton = new FlowLayout(FlowLayout.RIGHT, 5, 0);
	private JButton btnNext = new JButton();
	private JButton btnBack = new JButton();
	private JButton btnFirst = new JButton();
	private JButton btnExit = new JButton();

	private final Font SELECTED_FONT = new Font("Times New Roman", Font.BOLD
			| Font.ITALIC, 18);
	private final Font DESELECTED_FONT = new Font("Times New Roman", Font.BOLD
			| Font.ITALIC, 14);
	private final Color SELECTED_COLOR = Color.BLACK;
	private final Color DESELECTED_COLOR = Color.LIGHT_GRAY;

}
