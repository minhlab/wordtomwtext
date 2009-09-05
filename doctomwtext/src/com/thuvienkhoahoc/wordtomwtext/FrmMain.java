package com.thuvienkhoahoc.wordtomwtext;

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

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class FrmMain extends JFrame {

	public FrmMain() {
		super();
		initComponents();
	}

	private void initComponents() {
		setTitle("Wordtomwtext - By VLOS");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//chon tu lieu Word, Chinh sua noi dung, Tai len
		createTabbedPane(new String[] { "Ch\u1ECDn t\u00E0i li\u1EC7u Word",
				"Ch\u1EC9nh s\u1EEDa n\u1ED9i dung", "T\u1EA3i l\u00EAn" },
				new AbstractFunctionalPanel[] { new PnlFileChooser(),
				new PnlWikiEditor(), new PnlUploader() });

		pnlToolbar.setLayout(new BoxLayout(pnlToolbar, BoxLayout.LINE_AXIS));

		pnlToolbar.add(pnlTabRun);

		pnlToolbar.add(Box.createHorizontalGlue());
		
		lblUsername.setText(Application.getInstance().getUsername());
		pnlToolbar.add(lblUsername);
		// (dang nhap lai)
		lblSignIn.setText(" (\u0111\u0103ng nh\u1EADp l\u1EA1i)");
		lblSignIn.setForeground(Color.BLUE);
		lblSignIn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lblSignIn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				onRelogin();
			}
		});
		pnlToolbar.add(lblSignIn);

		getContentPane().add(pnlToolbar, BorderLayout.NORTH);

		getContentPane().add(pnlMainWrapper, BorderLayout.CENTER);

		// init button panel
		pnlButton.setLayout(layoutButton);
		//Quay lai
		btnBack.setText("Quay l\u1EA1i");
		btnBack.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				onBack();
			}
		});
		pnlButton.add(btnBack);
		//Tiep tuc
		btnNext.setText("Ti\u1EBFp t\u1EE5c");
		btnNext.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				onNext();
			}
		});
		pnlButton.add(btnNext);

		getContentPane().add(pnlButton, BorderLayout.SOUTH);

		pack();
	}

	private void createTabbedPane(String[] titles,
			AbstractFunctionalPanel[] panels) {
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

		setSelectedIndex(0, false);
	}

	protected void onNext() {
		if (!tabPanels[selectedIndex].work()) {
			return;
		}
		setSelectedIndex(selectedIndex + 1, false);
	}

	protected void onBack() {
		setSelectedIndex(selectedIndex - 1, true);
	}

	private void setSelectedIndex(int selectedIndex, boolean surpressLoad) {
		this.selectedIndex = selectedIndex;
		if (!surpressLoad) {
			tabPanels[selectedIndex].load();
		}
		layoutMainWrapper.show(pnlMainWrapper, tabLabels[selectedIndex]
				.getText());
		btnBack.setEnabled(selectedIndex > 0);
		btnNext.setEnabled(selectedIndex < tabLabels.length - 1);
		if (selectedIndex == tabLabels.length - 1) {
			btnNext.setText("Ho\u00E0n th\u00E0nh");//Hoan thanh
		} else {
			btnNext.setText("Ti\u1EBFp t\u1EE5c");//Tiep tuc
		}
	}

	protected void onRelogin() {
		setVisible(false);
		if (!Application.getInstance().login()) {
			System.exit(0);
		}
		lblUsername.setText(Application.getInstance().getUsername());
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
