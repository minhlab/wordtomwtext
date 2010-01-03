package org.apache.poi.ddf.converter;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.apache.poi.ddf.EscherContainerRecord;
import org.apache.poi.ddf.EscherOptRecord;
import org.apache.poi.ddf.EscherProperty;
import org.apache.poi.ddf.EscherRecord;
import org.apache.poi.ddf.EscherSimpleProperty;
import org.apache.poi.ddf.EscherSpRecord;
import org.apache.poi.ddf.EscherSpgrRecord;
import org.apache.poi.ddf.converter.autoshape.Autoshape;
import org.apache.poi.ddf.converter.autoshape.AutoshapeFactory;
import org.apache.poi.ddf.converter.autoshape.Call;
import org.apache.poi.ddf.converter.autoshape.DefaultAutoshapeFactory;
import org.apache.poi.ddf.converter.autoshape.Formula;

public class EscherRasterConverter {

	private AutoshapeFactory factory = DefaultAutoshapeFactory.INSTANCE;
	private int[] adjustments;
	private double[] formulaResults;
	private Autoshape autoshape;

	/**
	 * Convert group shape to raster image
	 * 
	 * @param spgrContainer
	 * @return raster image in memory
	 */
	public BufferedImage convert(EscherContainerRecord spgrContainer) {
		// create image
		EscherSpgrRecord spgr = (EscherSpgrRecord) spgrContainer
				.getChildById((short) 0xF009);
		int width = spgr.getRectX2() - spgr.getRectX1();
		int height = spgr.getRectY2() - spgr.getRectY1();
		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);

		// TODO draw according to z-index
		for (EscherRecord record : spgr.getChildRecords()) {
			if (record.getRecordId() == (short) 0xF003) {
				drawGroupShape(image, (EscherContainerRecord) record);
			}
			if (record.getRecordId() == (short) 0xF004) {
				drawShape(new ShapeContext(image),
						(EscherContainerRecord) record);
			}
		}

		return image;
	}

	/**
	 * Draw the shape described by the specified spContainer record
	 * 
	 * @param image
	 *            - image, maybe subimage
	 * @param record
	 *            - a spContainer record
	 */
	private void drawShape(ShapeContext context, EscherContainerRecord record) {
		// extract properties
		EscherOptRecord opt = (EscherOptRecord) record
				.getChildById((short) 0xF00B);
		if (opt != null) {
			extractProperties(opt);
		}
		opt = (EscherOptRecord) record.getChildById((short) 0xF121);
		if (opt != null) {
			extractProperties(opt);
		}
		opt = (EscherOptRecord) record.getChildById((short) 0xF122);
		if (opt != null) {
			extractProperties(opt);
		}

		// get autoshape
		EscherSpRecord sp = (EscherSpRecord) record
				.getChildById((short) 0xF00A);
		autoshape = factory.getAutoshape(sp.getShapeId());

		// compute formulas
		formulaResults = new double[autoshape.getFormulas().length];
		for (int i = 0; i < formulaResults.length; i++) {
			formulaResults[i] = computeFormula(i);
		}

		// draw paths
		for (Call command : autoshape.getCalls()) {
			execute(context, command);
		}
	}

	private void execute(ShapeContext context, Call call) {
		String command = call.getCommand();
		if ("m".equalsIgnoreCase(command)) {
			double x = translate(call.getParams()[0]);
			double y = translate(call.getParams()[1]);
			context.setCurrentPosition(x, y);
		} else if ("l".equalsIgnoreCase(command)) {
			for (int i = 0; i < call.getParams().length; i += 2) {
				double toX = translate(call.getParams()[i]);
				double toY = translate(call.getParams()[i + 1]);
				context.getGraphics2D().drawLine((int) context.getCurrentX(),
						(int) context.getCurrentY(), (int) toX, (int) toY);
				context.setCurrentPosition(toX, toY);
			}
		} else if ("m".equalsIgnoreCase(command)) {
			//XXX implement
		} else if ("m".equalsIgnoreCase(command)) {

		} else if ("m".equalsIgnoreCase(command)) {

		} else if ("m".equalsIgnoreCase(command)) {

		} else if ("m".equalsIgnoreCase(command)) {

		} else if ("m".equalsIgnoreCase(command)) {

		} else if ("m".equalsIgnoreCase(command)) {

		} else if ("m".equalsIgnoreCase(command)) {

		} else if ("m".equalsIgnoreCase(command)) {

		} else if ("m".equalsIgnoreCase(command)) {

		} else if ("m".equalsIgnoreCase(command)) {

		} else {
			System.out.println("Unknown command: " + command);
		}
	}

	private void extractProperties(EscherOptRecord opt) {
		// TODO extract more properties
		for (Object obj : opt.getEscherProperties()) {
			EscherProperty prop = (EscherProperty) obj;
			if (prop.getId() >= 327 && prop.getId() <= 336) {
				EscherSimpleProperty adj = (EscherSimpleProperty) prop;
				adjustments[prop.getId() - 327] = adj.getPropertyValue();
			}
		}
	}

	private double computeFormula(int i) {
		Formula formula = autoshape.getFormulas()[i];
		String operator = formula.getOperator();
		double operand1 = translate(formula.getOperands()[0]);
		double operand2 = (formula.getOperands().length >= 2 ? translate(formula
				.getOperands()[1])
				: 0);
		double operand3 = (formula.getOperands().length >= 3 ? translate(formula
				.getOperands()[2])
				: 0);
		;

		if ("sum".equalsIgnoreCase(operator)) {
			return operand1 + operand2 - operand3;
		}
		if ("product".equalsIgnoreCase(operator)
				|| "prod".equalsIgnoreCase(operator)) {
			return operand1 * operand2 / operand3;
		}
		if ("mid".equalsIgnoreCase(operator)) {
			return (operand1 + operand2) / 2;
		}
		if ("absolute".equalsIgnoreCase(operator)
				|| "abs".equalsIgnoreCase(operator)) {
			return Math.abs(operand1);
		}
		if ("min".equalsIgnoreCase(operator)) {
			return Math.min(operand1, operand2);
		}
		if ("max".equalsIgnoreCase(operator)) {
			return Math.max(operand1, operand2);
		}
		if ("if".equalsIgnoreCase(operator)) {
			return (operand1 > 0 ? operand2 : operand3);
		}
		if ("sqrt".equalsIgnoreCase(operator)) {
			return Math.sqrt(operand1);
		}
		if ("mod".equalsIgnoreCase(operator)) {
			return Math.sqrt(operand1 * operand1 + operand2 * operand2
					+ operand3 * operand3);
		}
		if ("sin".equalsIgnoreCase(operator)) {
			return operand1 * Math.sin(operand2);
		}
		if ("cos".equalsIgnoreCase(operator)) {
			return operand1 * Math.cos(operand2);
		}
		if ("atan2".equalsIgnoreCase(operator)) {
			return Math.atan2(operand2, operand1);
		}
		if ("sinatan2".equalsIgnoreCase(operator)) {
			return operand1 * Math.sin(Math.atan2(operand3, operand2));
		}
		if ("cosatan2".equalsIgnoreCase(operator)) {
			return operand1 * Math.cos(Math.atan2(operand3, operand2));
		}

		System.out.println("Unknown formula: " + formula);
		return 0;
	}

	private double translate(String operand) {
		if (operand.startsWith("#")) {
			return adjustments[Integer.parseInt(operand.substring(1))];
		}
		if (operand.startsWith("@")) {
			return formulaResults[Integer.parseInt(operand.substring(1))];
		}
		return Double.parseDouble(operand);
	}

	/**
	 * Draw the shape described by the specified spContainer record
	 * 
	 * @param image
	 *            - image, maybe subimage
	 * @param record
	 *            - a spContainer record
	 */
	private void drawGroupShape(BufferedImage image,
			EscherContainerRecord record) {
		// get subimage
		EscherSpgrRecord spgr = (EscherSpgrRecord) record
				.getChildById((short) 0xF009);
		int x = spgr.getRectX1();
		int y = spgr.getRectY1();
		int width = spgr.getRectX2() - x;
		int height = spgr.getRectY2() - y;
		BufferedImage subimage = image.getSubimage(x, y, width, height);

		// draw children
		// TODO draw according to z-index
		for (EscherRecord child : record.getChildRecords()) {
			if (child.getRecordId() == (short) 0xF003) {
				drawGroupShape(subimage, (EscherContainerRecord) child);
			}
			if (child.getRecordId() == (short) 0xF004) {
				drawShape(new ShapeContext(subimage),
						(EscherContainerRecord) child);
			}
		}
	}

	private static class ShapeContext {

		private BufferedImage image;
		private Graphics graphics;
		double currentX = 0;
		private double currentY = 0;

		public ShapeContext(BufferedImage image) {
			super();
			this.image = image;
			this.graphics = image.getGraphics();
			//TODO set affline transform
		}

		public BufferedImage getImage() {
			return image;
		}

		public Graphics2D getGraphics2D() {
			return (Graphics2D) graphics;
		}

		public int getWidth() {
			return image.getWidth();
		}

		public int getHeight() {
			return image.getHeight();
		}

		public double getCurrentX() {
			return currentX;
		}

		public double getCurrentY() {
			return currentY;
		}

		public void setCurrentPosition(double x, double y) {
			currentX = x;
			currentY = y;
		}

	}

}
