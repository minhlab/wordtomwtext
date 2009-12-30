package com.thuvienkhoahoc.wordtomwtext.ui;

import java.awt.Color;
import java.awt.Component;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import net.sourceforge.jwbf.contentRep.mw.SimpleArticle;

import com.thuvienkhoahoc.wordtomwtext.Application;
import com.thuvienkhoahoc.wordtomwtext.data.Image;
import com.thuvienkhoahoc.wordtomwtext.data.Page;
import com.thuvienkhoahoc.wordtomwtext.data.Project;

@SuppressWarnings("serial")
public class PnlUploader extends AbstractFunctionalPanel {

	private int maxThread = 4;

	private int totalTasks;
	private int completedTasks;

	private Project project;

	public PnlUploader() {
		initComponents();
	}

	private void initComponents() {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		modCheckList = new DefaultTableModel(
				new String[] { "Tên", "Trạng thái" }, 0);
		tblCheckList.setModel(modCheckList);
		tblCheckList.setDefaultRenderer(Object.class, new DefaultRenderer());
		add(new JScrollPane(tblCheckList), 0.0);

		this.add(barProgress, 0.0);

		lblProgress.setHorizontalAlignment(SwingConstants.LEFT);
		this.add(lblProgress, 0.0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void load(Object obj) {
		this.project = (Project) obj;

		modCheckList.getDataVector().clear();
		for (Page page : project.getPages()) {
			Vector<Object> row = new Vector<Object>();
			row.add(page);
			row.add(STATE_READY);
			modCheckList.getDataVector().add(row);
		}
		for (Image image : project.getImages()) {
			Vector<Object> row = new Vector<Object>();
			row.add(image);
			row.add(STATE_READY);
			modCheckList.getDataVector().add(row);
		}
		modCheckList.fireTableDataChanged();

		setState(STATE_RUNNING);
		lblProgress.setForeground(Color.BLACK);
		new Worker().execute();
	}

	@SuppressWarnings("unchecked")
	private void setStateFromOtherThread(final Object obj, final int state) {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {

				@Override
				public void run() {
					for (int rowIndex = 0; rowIndex < modCheckList
							.getRowCount(); rowIndex++) {
						Vector row = (Vector) modCheckList.getDataVector().get(
								rowIndex);
						Object item = row.get(0);
						if (item == obj) {
							row.set(1, state);
							modCheckList.fireTableCellUpdated(rowIndex, 1);
							completedTasks++;
							barProgress.setValue(completedTasks * 100
									/ totalTasks);
							return;
						}
					}
				}
			});
		} catch (Exception e) {
			// không ảnh hưởng lắm đến chương trình
			e.printStackTrace();
		}
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

	private class PageChecker implements Runnable {

		private Page page;

		public PageChecker(Page page) {
			super();
			this.page = page;
		}

		@Override
		public void run() {
			try {
				SimpleArticle article = Application.getInstance().getBot()
						.readContent(page.getLabel());
				if (article.getText().trim().length() <= 0) {
					setStateFromOtherThread(page, STATE_FINISHED);
				} else {
					setStateFromOtherThread(page, STATE_ERROR);
				}
			} catch (Exception e) {
				setStateFromOtherThread(page, STATE_ERROR);
				e.printStackTrace();
			}
		}

	}

	private class ImageChecker implements Runnable {

		private Image image;

		public ImageChecker(Image image) {
			super();
			this.image = image;
		}

		@Override
		public void run() {
			try {
				String path = Application.getInstance().getBot().getImageInfo(
						image.getLabel());
				if (path.length() <= 0) {
					setStateFromOtherThread(image, STATE_FINISHED);
				} else {
					setStateFromOtherThread(image, STATE_ERROR);
				}
			} catch (Exception e) {
				setStateFromOtherThread(image, STATE_ERROR);
				e.printStackTrace();
			}
		}

	}

	private class ImageUploader implements Runnable {

		private Image image;

		public ImageUploader(Image image) {
			super();
			this.image = image;
		}

		@Override
		public void run() {
			try {
				Application.getInstance().getBot().uploadFile(image);
				setStateFromOtherThread(image, STATE_FINISHED);
			} catch (Exception e) {
				setStateFromOtherThread(image, STATE_ERROR);
				e.printStackTrace();
			}
		}

	}

	private class PageUploader implements Runnable {

		private Page page;

		public PageUploader(Page page) {
			super();
			this.page = page;
		}

		@Override
		public void run() {
			try {
				Application.getInstance().getBot().writeContent(page);
				setStateFromOtherThread(page, STATE_FINISHED);
			} catch (Exception e) {
				setStateFromOtherThread(page, STATE_ERROR);
				e.printStackTrace();
			}
		}

	}

	private class Worker extends SwingWorker<String, Object> {

		@SuppressWarnings("unchecked")
		@Override
		protected String doInBackground() throws Exception {
			ThreadPoolExecutor pool = null;
			try {
				totalTasks = (project.getImages().size() + project.getPages()
						.size()) * 2;
				completedTasks = 0;

				// kiểm tra từng phần tử
				pool = new ThreadPoolExecutor(2, maxThread, 1,
						TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
				publish("Kiểm tra tính khả dụng của tên trang...");
				for (Page page : project.getPages()) {
					pool.submit(new PageChecker(page));
				}
				for (Image image : project.getImages()) {
					pool.submit(new ImageChecker(image));
				}
				pool.shutdown();
				pool.awaitTermination(totalTasks, TimeUnit.MINUTES);

				// duyệt và thiết lập lại
				for (Object obj : modCheckList.getDataVector()) {
					Vector row = (Vector) obj;
					if (((Integer) row.get(1)).intValue() != STATE_FINISHED) {
						return "Có phần tử trùng tên, xin vui lòng quay lại sửa.";
					}
					row.set(1, STATE_READY);
				}

				// tải lên
				pool = new ThreadPoolExecutor(2, maxThread, 1,
						TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
				publish("Tải trang và hình ảnh lên máy phục vụ...");
				for (Page page : project.getPages()) {
					pool.submit(new PageUploader(page));
				}
				for (Image image : project.getImages()) {
					pool.submit(new ImageUploader(image));
				}
				pool.shutdown();
				pool.awaitTermination(totalTasks, TimeUnit.MINUTES);

				publish(100);
				publish("Hoàn thành.");
				return null;
			} catch (InterruptedException e) {
				e.printStackTrace();
				return "Lỗi nội bộ xảy ra, tác vụ của bạn chưa được hoàn thành.";
			} finally {
				if (pool != null) {
					pool.shutdown();
				}
			}
		}

		@Override
		protected void done() {
			try {
				String mess = get();
				if (mess == null) {
					setState(STATE_FINISHED);
				} else {
					lblProgress.setText(mess);
					lblProgress.setForeground(Color.RED);
					setState(STATE_ERROR);
				}
			} catch (Exception e) {
				// never happen
				e.printStackTrace();
			}
		}

		@Override
		protected void process(List<Object> chunks) {
			for (Object value : chunks) {
				if (value instanceof String) {
					lblProgress.setText((String) value);
				} else if (value instanceof Integer) {
					barProgress.setValue((Integer) value);
				}
			}
		}
	}

	private static ImageIcon createIcon(String name) {
		return new ImageIcon(PnlUploader.class.getResource("../images/" + name));
	}

	private class DefaultRenderer extends DefaultTableCellRenderer {
		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			JLabel label = (JLabel) super.getTableCellRendererComponent(table,
					value, isSelected, hasFocus, row, column);
			if (value instanceof Integer) {
				switch (((Integer) value).intValue()) {
				case STATE_ERROR:
					label.setText("Lỗi");
					label.setIcon(icoError);
					break;
				case STATE_FINISHED:
					label.setText("Hoàn thành");
					label.setIcon(icoFinished);
					break;
				case STATE_READY:
					label.setText("Sẵn sàng");
					label.setIcon(icoReady);
					break;
				case STATE_RUNNING:
					label.setText("Đang chạy");
					label.setIcon(icoRunning);
					break;
				}
			} else {
				label.setIcon(null);
			}
			return label;
		}
	}

	/*
	 * Components
	 */
	private JProgressBar barProgress = new JProgressBar();
	private JLabel lblProgress = new JLabel();
	private JTable tblCheckList = new JTable();
	private DefaultTableModel modCheckList = new DefaultTableModel();

	private ImageIcon icoError = createIcon("error.gif");
	private ImageIcon icoFinished = createIcon("finished.png");
	private ImageIcon icoReady = createIcon("ready.png");
	private ImageIcon icoRunning = createIcon("running.png");

}
