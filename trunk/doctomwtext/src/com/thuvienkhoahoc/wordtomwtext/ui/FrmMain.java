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
//		this.setLocationRelativeTo(null);
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
			tabLabels[i].setFont(new Font("Times New Roman", Font.BOLD
					| Font.ITALIC, 14));
			tabLabels[i].setForeground(Color.LIGHT_GRAY);
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
		pnlButton.add(btnBack);

		btnNext.setText("Tiếp tục");
		pnlButton.add(btnNext);

		getContentPane().add(pnlButton, BorderLayout.SOUTH);

		pack();
	}

	private void handleEvents() {
		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				onWindowClosing();
			}
		});
		lblSignIn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					onRelogin();
				}
			}
		});
		btnBack.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				onBack();
			}
		});
		btnNext.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				onNext();
			}
		});
		PropertyChangeListener tabPropertyChangeListener = new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getSource() == getSelectedTab()) {
					if ("state".equals(evt.getPropertyName())) {
						updateState();
					}
				}
			}
		};
		for (int i = 0; i < tabPanels.length; i++) {
			tabPanels[i].addPropertyChangeListener(tabPropertyChangeListener);
		}
	}

	protected void onWindowClosing() {
		if (getSelectedTab().canClose()) {
			Application.getInstance().exit(0);
		}
	}

	protected void onNext() {
		if (getSelectedTab().canNext() && getSelectedTab().next()) {
			setSelectedIndex((selectedIndex + 1) % tabPanels.length, false,
					getSelectedTab().getResult());
		}
	}

	protected void onBack() {
		if (getSelectedTab().canBack()) {
			setSelectedIndex(selectedIndex - 1, true, null);
		}
	}

	private AbstractFunctionalPanel getSelectedTab() {
		return tabPanels[selectedIndex];
	}

	private void setSelectedIndex(int selectedIndex, boolean surpressLoad,
			Object data) {
		tabLabels[this.selectedIndex].setForeground(Color.LIGHT_GRAY);
		tabLabels[selectedIndex].setForeground(Color.BLACK);
		this.selectedIndex = selectedIndex;
		if (!surpressLoad) {
			tabPanels[selectedIndex].load(data);
		}
		layoutMainWrapper.show(pnlMainWrapper, tabLabels[selectedIndex]
				.getText());
		updateState();
	}

	protected void onRelogin() {
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

	private void updateState() {
		btnBack.setEnabled(getSelectedTab().canBack());
		btnNext.setEnabled(getSelectedTab().canNext());
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

}
