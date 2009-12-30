package com.thuvienkhoahoc.wordtomwtext.ui;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import com.thuvienkhoahoc.wordtomwtext.Application;
import com.thuvienkhoahoc.wordtomwtext.data.Page;
import com.thuvienkhoahoc.wordtomwtext.data.Project;

@SuppressWarnings("serial")
public class PnlFinished extends AbstractFunctionalPanel {

	public PnlFinished() {
		initComponents();
		handleEvents();
		setState(STATE_FINISHED);
	}

	private void initComponents() {
		this.setLayout(new BorderLayout());

		txtMessage.setContentType("text/html");
		txtMessage.setEditable(false);
		this.add(new JScrollPane(txtMessage), BorderLayout.CENTER);
	}

	private void handleEvents() {
		txtMessage.addHyperlinkListener(new HyperlinkListener() {

			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					try {
						Desktop.getDesktop()
								.browse(new URI(e.getDescription()));
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		});
	}

	@Override
	public Object getResult() {
		return null;
	}

	@Override
	public void load(Object data) {
		Project project = (Project) data;
		StringBuilder sb = new StringBuilder("<h1>Xin chúc mừng!</h1>"
				+ "Bạn đã tải lên thành công các bài viết:<br/>");
		sb.append("<ul>");
		for (Page page : project.getPages()) {
			sb.append("<li>");
			generateLink(sb, page);
			sb.append("</li>");
		}
		sb.append("</ul>");
		sb.append("<h3>Hãy nhấn <i>\"Về đầu\"</i> để "
				+ "chuyển đổi và tải lên những tệp khác!</h3>");
		txtMessage.setText(sb.toString());
	}

	private void generateLink(StringBuilder sb, Page page) {
		try {
			sb.append("<a href=\"").append(
					Application.getInstance().getSiteurl()).append(
					"index.php?title=").append(
					URLEncoder.encode(page.getLabel(), "UTF-8")).append(
					"\">").append(page.getLabel()).append("</a>");
		} catch (UnsupportedEncodingException e) {
			sb.append(page.getLabel());
			e.printStackTrace();
		}
		sb.append("<br/>");

		if (!page.getChildren().isEmpty()) {
			sb.append("<ul>");
			for (Page subpage : page.getChildren()) {
				sb.append("<li>");
				generateLink(sb, subpage);
				sb.append("</li>");
			}
			sb.append("<ul>");
		}
	}
	
	@Override
	public boolean canNext() {
		return false;
	}

	private JTextPane txtMessage = new JTextPane();

}
