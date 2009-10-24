package com.thuvienkhoahoc.wordtomwtext.ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import net.sourceforge.jwbf.contentRep.mw.SimpleArticle;

import com.thuvienkhoahoc.wordtomwtext.Application;
import com.thuvienkhoahoc.wordtomwtext.data.Image;
import com.thuvienkhoahoc.wordtomwtext.data.Page;
import com.thuvienkhoahoc.wordtomwtext.data.Project;

@SuppressWarnings("serial")
public class PnlUploader extends AbstractFunctionalPanel {

	private Project project;

	public PnlUploader() {
		initComponents();
	}

	private void initComponents() {
		this.setLayout(new BorderLayout());

		BorderLayout layProgress = new BorderLayout();
		layProgress.setHgap(5);

		pnlProgress.setBorder(BorderFactory.createEmptyBorder(3, 5, 5, 5));
		pnlProgress.setLayout(layProgress);

		pnlProgress.add(barProgress, BorderLayout.CENTER);
		pnlProgress.add(lblProgress, BorderLayout.EAST);

		add(pnlProgress, BorderLayout.NORTH);

		txtMessage.setFont(new Font("Arial", Font.PLAIN, 14));
		txtMessage.setEditable(false);
		txtMessage.setLineWrap(true);
		txtMessage.setWrapStyleWord(true);
		add(new JScrollPane(txtMessage), BorderLayout.CENTER);
	}

	private void appendMessage(String msg) {
		txtMessage.append(msg);
		txtMessage.setCaretPosition(txtMessage.getDocument().getLength() - 1);
	}

	@Override
	public void load(Object obj) {
		this.project = (Project) obj;
		setState(STATE_RUNNING);
		txtMessage.setText("");
		new Uploader().execute();
	}

	@Override
	public Project getResult() {
		return project;
	}

	@Override
	public boolean canClose() {
		if (getState() == STATE_RUNNING) {
			return false;
		}
		if (getState() == STATE_FINISHED) {
			return true;
		}
		return JOptionPane
				.showConfirmDialog(
						this,
						"Dữ liệu của bạn chưa được tải lên hoàn toàn và sẽ biến mất\n"
								+ "nếu chương trình bị đóng. Bạn có chắc chắn muốn đóng chương trình?",
						"Xác nhận đóng chương trình",
						JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION;
	}

	/*
	 * Workers
	 */
	private class Uploader extends SwingWorker<Boolean, String> implements
			PropertyChangeListener {

		public Uploader() {
			this.addPropertyChangeListener(this);
		}

		@Override
		protected Boolean doInBackground() throws Exception {
			boolean error = false;
			int counter = 0, total = (project.getPages().size() + project
					.getImages().size()) * 2;
			publish("Kiểm tra tên bài viết và ảnh...\n");

			for (Page page : project.getPages()) {
				publish("\t" + page.getLabel() + "... ");

				SimpleArticle article = Application.getInstance().getBot()
						.readContent(page.getLabel());
				if (article.getText().trim().length() <= 0) {
					publish("OK\n");
				} else {
					publish("TRÙNG TÊN!\n");
					error = true;
				}

				setProgress(++counter * 100 / total);
			}

			for (Image image : project.getImages()) {
				publish("\t" + image.getLabel() + "... ");

				String path = Application.getInstance().getBot().getImageInfo(
						image.getLabel());
				if (path.length() <= 0) {
					publish("OK\n");
				} else {
					publish("TRÙNG TÊN!\n");
					error = true;
				}

				setProgress(++counter * 100 / total);
			}

			if (error) {
				JOptionPane.showMessageDialog(PnlUploader.this,
						"Ảnh hoặc ài viết của bạn trùng tên với bài trên "
								+ Application.getInstance().getSitename()
								+ ".\nXin hãy sửa lại tên ảnh / bài viết.",
						"Ảnh / Bài viết bị trùng tên",
						JOptionPane.ERROR_MESSAGE);
				return false;
			}

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

			if (error) {
				JOptionPane
						.showMessageDialog(
								PnlUploader.this,
								"Chương trình gặp lỗi khi tải lên bài viết và/hoặc hình ảnh của bạn.\nHãy kiểm tra và tải lại bằng tay.",
								"Có lỗi khi tải lên", JOptionPane.ERROR_MESSAGE);
				return false;
			}

			publish("Xin chúc mừng! Toàn bộ bài viết và hình ảnh của bạn đã được tải thành công lên "
					+ Application.getInstance().getSitename()
					+ ". Cảm ơn bạn đã đóng góp!");
			return true;
		}

		@Override
		protected void process(List<String> chunks) {
			for (String chunk : chunks) {
				appendMessage(chunk);
			}
		}

		@Override
		protected void done() {
			try {
				setState(get() ? STATE_FINISHED : STATE_ERROR);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if ("progress".equals(evt.getPropertyName())) {
				barProgress.setValue((Integer) evt.getNewValue());
				lblProgress.setText(evt.getNewValue() + "%");
			}
		}

	};

	/*
	 * Components
	 */
	private JTextArea txtMessage = new JTextArea();
	private JProgressBar barProgress = new JProgressBar();
	private JLabel lblProgress = new JLabel();
	private JPanel pnlProgress = new JPanel();

}
