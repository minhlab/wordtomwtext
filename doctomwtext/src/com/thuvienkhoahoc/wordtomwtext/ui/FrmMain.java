package com.thuvienkhoahoc.wordtomwtext.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.thuvienkhoahoc.wordtomwtext.Application;

@SuppressWarnings("serial")
public class FrmMain extends JFrame {

	public FrmMain() {
		super();
		initComponents();
	}

	private void initComponents() {
		this.setTitle("Wordtomwtext - By VLOS");
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				onWindowClosing();
			}
		});

		String[] titles = new String[] { "Chọn tài liệu Word",
				"Chỉnh sửa nội dung", "Tải lên" };
		AbstractFunctionalPanel[] panels = new AbstractFunctionalPanel[] {
				new PnlFileChooser(), new PnlProjectEditor(), new PnlUploader() };

		pnlTabRun.setLayout(layoutTabRun);
		tabLabels = new JLabel[titles.length];
		for (int i = 0; i < titles.length; i++) {
			tabLabels[i] = new JLabel(titles[i]);
			pnlTabRun.add(tabLabels[i]);
		}

		pnlMainWrapper.setLayout(layoutMainWrapper);
		tabPanels = panels;
		for (int i = 0; i < titles.length; i++) {
			pnlMainWrapper.add(panels[i], titles[i]);
		}

		setSelectedIndex(0, false, null);

		pnlToolbar.setLayout(new BoxLayout(pnlToolbar, BoxLayout.LINE_AXIS));

		pnlToolbar.add(pnlTabRun);

		pnlToolbar.add(Box.createHorizontalGlue());

		lblUsername.setText(Application.getInstance().getUsername());
		lblUsername.setToolTipText("tại "
				+ Application.getInstance().getSitename());
		pnlToolbar.add(lblUsername);

		lblSignIn.setText(" (đăng nhập lại)");
		lblSignIn.setForeground(Color.BLUE);
		lblSignIn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lblSignIn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					onRelogin();
				}
			}
		});
		pnlToolbar.add(lblSignIn);

		getContentPane().add(pnlToolbar, BorderLayout.NORTH);

		getContentPane().add(pnlMainWrapper, BorderLayout.CENTER);

		// init button panel
		pnlButton.setLayout(layoutButton);
		pnlButton.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

		btnBack.setText("Quay lại");
		btnBack.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				onBack();
			}
		});
		pnlButton.add(btnBack);

		btnNext.setText("Tiếp tục");
		btnNext.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				onNext();
			}
		});
		pnlButton.add(btnNext);

		getContentPane().add(pnlButton, BorderLayout.SOUTH);

		pack();
	}

	protected void onWindowClosing() {
		if (JOptionPane
				.showConfirmDialog(
						this,
						"Mọi tài liệu bạn đang soạn thảo (nếu có) sẽ bị mất. Bạn có chắc muốn thoát khỏi chương trình?",
						"Xác nhận đóng chương trình", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			Application.getInstance().exit(0);
		}
	}

	protected void onNext() {
		if (!tabPanels[selectedIndex].next()) {
			return;
		}
		setSelectedIndex(selectedIndex + 1, false, tabPanels[selectedIndex]
				.getResult());
	}

	protected void onBack() {
		setSelectedIndex(selectedIndex - 1, true, null);
	}

	private void setSelectedIndex(int selectedIndex, boolean surpressLoad,
			Object data) {
		this.selectedIndex = selectedIndex;
		if (!surpressLoad) {
			tabPanels[selectedIndex].load(data);
		}
		layoutMainWrapper.show(pnlMainWrapper, tabLabels[selectedIndex]
				.getText());
		btnBack.setEnabled(selectedIndex > 0);
		btnNext.setEnabled(selectedIndex < tabLabels.length - 1);
		if (selectedIndex == tabLabels.length - 1) {
			btnNext.setText("Kết thúc");
		} else {
			btnNext.setText("Tiếp tục");
		}
	}

	protected void onRelogin() {
		setVisible(false);
		Application.getInstance().logout();
		if (!Application.getInstance().showLoginDialog()) {
			Application.getInstance().exit(0);
		}
		lblUsername.setText(Application.getInstance().getUsername());
		lblUsername.setToolTipText("tại "
				+ Application.getInstance().getSitename());
		setVisible(true);
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
