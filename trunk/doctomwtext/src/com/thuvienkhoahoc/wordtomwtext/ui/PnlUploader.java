package com.thuvienkhoahoc.wordtomwtext.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import net.sourceforge.jwbf.contentRep.mw.SimpleArticle;

import com.thuvienkhoahoc.wordtomwtext.Application;
import com.thuvienkhoahoc.wordtomwtext.data.Image;
import com.thuvienkhoahoc.wordtomwtext.data.Page;
import com.thuvienkhoahoc.wordtomwtext.data.Project;

@SuppressWarnings("serial")
public class PnlUploader extends AbstractFunctionalPanel<Project, Void> {

	private boolean done;
	private Project project;

	public PnlUploader() {
		initComponents();
	}

	private void initComponents() {
		setLayout(new BorderLayout());

		txtMessage.setEditable(false);
		add(txtMessage, BorderLayout.CENTER);

		pnlProgress.setBorder(BorderFactory.createEmptyBorder(3, 5, 5, 5));
		pnlProgress.setLayout(new FlowLayout(FlowLayout.RIGHT));

		pnlProgress.add(barProgress);

		add(pnlProgress, BorderLayout.SOUTH);
	}

	private void appendMessage(String msg) {
		txtMessage.append(msg);
		txtMessage.setCaretPosition(txtMessage.getDocument().getLength() - 1);
	}

	@Override
	public void load(Project project) {
		this.project = project;
		done = false;
		Uploader uploader = new Uploader();
		uploader.addPropertyChangeListener(new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if ("progress".equals(evt.getPropertyName())) {
					barProgress.setValue((Integer)evt.getNewValue());
				}
			}
		});
		uploader.execute();
		txtMessage.setText("");
	}

	@Override
	public boolean next() {
		return done;
	}

	@Override
	public Void getResult() {
		return null;
	}

	/*
	 * Workers
	 */
	private class Uploader extends SwingWorker<Void, String> {

		private boolean conflict = false, error = false;

		@Override
		protected Void doInBackground() throws Exception {
			int counter = 0, total = project.getPages().size()
					+ project.getImages().size();
			publish("Kiểm tra tên bài viết và ảnh...\n");

			for (Page page : project.getPages()) {
				publish("\t" + page.getLabel() + "... ");

				SimpleArticle article = Application.getInstance().getBot()
						.readContent(page.getLabel());
				if (article.getText().trim().length() <= 0) {
					publish("OK\n");
				} else {
					publish("TRÙNG TÊN!\n");
					conflict = true;
				}

				setProgress(++counter * 100 / total);
			}

			for (Image image : project.getImages()) {
				publish("\t" + image.getLabel() + "... ");

				SimpleArticle article = Application.getInstance().getBot()
						.readContent("Image:" + image.getLabel());
				if (article.getText().trim().length() <= 0) {
					publish("OK\n");
				} else {
					publish("TRÙNG TÊN!\n");
					conflict = true;
				}

				setProgress(++counter * 100 / total);
			}

			if (conflict) {
				JOptionPane.showMessageDialog(PnlUploader.this,
						"Bài viết bị trùng tên",
						"Bài viết của bạn trùng tên với bài trên "
								+ Application.getInstance().getSitename()
								+ ". Xin hãy sửa lại tên bài viết.",
						JOptionPane.ERROR_MESSAGE);
				return null;
			}
			
			counter = 0;
			total = project.getPages().size()
			+ project.getImages().size();

			publish("Tải lên " + Application.getInstance().getSitename()
					+ "...\n");
		
			for (Page page : project.getPages()) {
				publish("\t" + page.getLabel() + "... ");
		
				try {
					Application.getInstance().getBot().writeContent(page);
					publish("OK\n");
				} catch (Exception ex) {
					error = true;
					publish("LỖI: " + ex.getMessage() + "\n");
					ex.printStackTrace();
				}
		
				setProgress(++counter * 100 / total);
			}
		
			for (Image image : project.getImages()) {
				publish("\t" + image.getLabel() + "... ");
		
				try {
					Application.getInstance().getBot().uploadFile(image);
					publish("OK\n");
				} catch (Exception ex) {
					error = true;
					publish("LỖI: " + ex.getMessage() + "\n");
					ex.printStackTrace();
				}
		
				setProgress(++counter * 100 / total);
			}

			if (!error) {
				JOptionPane.showMessageDialog(PnlUploader.this,
						"Xin chúc mừng!",
						"Toàn bộ bài viết và hình ảnh của bạn đã được tải thành công lên "
								+ Application.getInstance().getSitename()
								+ ". Cảm ơn bạn đã đóng góp!",
						JOptionPane.INFORMATION_MESSAGE);
			} else {
				JOptionPane
						.showMessageDialog(
								PnlUploader.this,
								"Có lỗi khi tải lên",
								"Chương trình gặp lỗi khi tải lên bài viết và/hoặc hình ảnh của bạn. Hãy kiểm tra lại và tải lên bằng tay nếu cần.",
								JOptionPane.ERROR_MESSAGE);
			}

			return null;
		}

		@Override
		protected void process(List<String> chunks) {
			for (String chunk : chunks) {
				appendMessage(chunk);
			}
		}

	};

	/*
	 * Components
	 */
	private JTextArea txtMessage = new JTextArea();
	private JProgressBar barProgress = new JProgressBar();
	private JPanel pnlProgress = new JPanel();

}
