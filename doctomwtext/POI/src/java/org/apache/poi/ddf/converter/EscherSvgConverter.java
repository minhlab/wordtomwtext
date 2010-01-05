package org.apache.poi.ddf.converter;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.apache.poi.ddf.EscherChildAnchorRecord;
import org.apache.poi.ddf.EscherContainerRecord;
import org.apache.poi.ddf.EscherOptRecord;
import org.apache.poi.ddf.EscherProperty;
import org.apache.poi.ddf.EscherRecord;
import org.apache.poi.ddf.EscherSimpleProperty;
import org.apache.poi.ddf.EscherSpRecord;
import org.apache.poi.ddf.EscherSpgrRecord;

public class EscherSvgConverter {

	/**
	 * Constant for converting EMU to pixel.
	 */
	private static final double ETP = 100.0;

	/**
	 * Size of autoshape coordinate system
	 */
	private static final double SPACE_SIZE = 21600.0;

	private AutoshapeFactory factory = AutoshapeFactory.INSTANCE;
	
	private int[] adjustments;
	private double[] formulaResults;
	private Autoshape autoshape;

	/**
	 * Output of the converter
	 */
	private Writer out;

	private int level;

	private void print(String s) {
		try {
			out.write(s);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void println(String s) {
		indent();
		print(s);
		print("\n");
	}

	private void indent() {
		for (int i = 0; i < level; i++) {
			print("  ");
		}
	}

	private void open(String s) {
		println(s);
		level++;
	}

	private void close(String s) {
		level--;
		println(s);
	}

	public String convert(EscherContainerRecord spgrContainer) {
		StringWriter out = new StringWriter();
		convert(spgrContainer, out);
		return out.toString();
	}

	/**
	 * Convert group shape to raster image
	 * 
	 * @param spgrContainer
	 * @return raster image in memory
	 */
	public void convert(EscherContainerRecord spgrContainer, Writer out) {
		// init
		this.out = out;
		this.level = 0;

		// get info
		EscherSpgrRecord spgr = (EscherSpgrRecord) spgrContainer
				.getChildById((short) 0xF009);
		int x = spgr.getRectX1();
		int w = spgr.getRectX2() - x;
		int y = spgr.getRectY1();
		int h = spgr.getRectY2() - y;

		// convert
		// TODO draw according to z-index
		println("<?xml version=\"1.0\" standalone=\"no\"?>");
		println("<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\""
				+ " \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">");
		open("<svg width=\"" + w + "\" height=\"" + h + "\"" + " viewBox=\""
				+ x + " " + y + " " + w + " " + h + "\""
				+ " version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\">");
		for (EscherRecord record : spgr.getChildRecords()) {
			if (record.getRecordId() == (short) 0xF003) {
				parseGroupShape((EscherContainerRecord) record);
			}
			if (record.getRecordId() == (short) 0xF004) {
				parseAtomShape(new ShapeContext(w, h),
						(EscherContainerRecord) record);
			}
		}
		close("</svg>");
	}

	/**
	 * Draw the shape described by the specified spContainer record
	 * 
	 * @param image
	 *            - image, maybe subimage
	 * @param container
	 *            - a spContainer record
	 */
	private void parseAtomShape(ShapeContext context,
			EscherContainerRecord container) {
		// get autoshape
		EscherSpRecord sp = (EscherSpRecord) container
				.getChildById((short) 0xF00A);
		autoshape = factory.getAutoshape(sp.getShapeId());
		if (autoshape == null) {
			return;
		}

		// extract properties
		EscherOptRecord opt = (EscherOptRecord) container
				.getChildById((short) 0xF00B);
		if (opt != null) {
			extractProperties(opt);
		}
		opt = (EscherOptRecord) container.getChildById((short) 0xF121);
		if (opt != null) {
			extractProperties(opt);
		}
		opt = (EscherOptRecord) container.getChildById((short) 0xF122);
		if (opt != null) {
			extractProperties(opt);
		}
		boolean noStroke = false, noFill = false;
		for (String[] call : autoshape.calls) {
			if ("ns".equalsIgnoreCase(call[0])
					|| "nostroke".equalsIgnoreCase(call[0])) {
				noStroke = true;
			} else if ("nf".equalsIgnoreCase(call[0])
					|| "nofill".equalsIgnoreCase(call[0])) {
				noFill = true;
			}
		}
		EscherChildAnchorRecord anchor = (EscherChildAnchorRecord) container
				.getChildById((short) 0xF00F);
		double x = anchor.getDx1() / ETP;
		double y = anchor.getDy1() / ETP;
		double w = anchor.getDx2() / ETP - x;
		double h = anchor.getDy2() / ETP - y;

		// compute formulas
		formulaResults = new double[autoshape.formulas.length];
		for (int i = 0; i < formulaResults.length; i++) {
			formulaResults[i] = computeFormula(i);
		}

		StringBuilder style = new StringBuilder();
		if (noFill || noStroke) {
			style.append(" style=\"");
			if (noFill) {
				style.append("fill: none;");
			}
			if (noStroke) {
				style.append("stroke: none;");
			}
			style.append("\"");
		}

		// draw paths
		open("<svg x=\"" + x + "\" y=\"" + y + "\" viewBox=\"" + x + " " + y
				+ " " + w + " " + h + "\"" + style + ">");
		int start = 0;
		for (int i = 0; i < autoshape.calls.length; i++) {
			String command = autoshape.calls[i][0];
			if ("e".equalsIgnoreCase(command)
					|| "end".equalsIgnoreCase(command)) {
				parsePath(context, autoshape.calls, start, i);
			}
			start = i + 1;
		}
		if (start < autoshape.calls.length) {
			parsePath(context, autoshape.calls, start, autoshape.calls.length);
		}
		close("</svg>");
	}

	private void parsePath(ShapeContext context, String[][] calls, int start,
			int end) {
		indent();
		print("<path d=\"");
		for (int i = start; i < end; i++) {
			parseCall(context, calls[i]);
		}
		print("\" />\n");
	}

	private void parseCall(ShapeContext context, String[] call) {
		if ("m".equalsIgnoreCase(call[0]) || "moveto".equalsIgnoreCase(call[0])) {

			double x = translate(call[1]) * context.xfactor;
			double y = translate(call[2]) * context.yfactor;
			print("M " + x + " " + y + " ");

		} else if ("l".equalsIgnoreCase(call[0])
				|| "lineto".equalsIgnoreCase(call[0])) {

			print("L ");
			for (int i = 1; i < call.length; i += 2) {
				double x = translate(call[i]) * context.xfactor;
				double y = translate(call[i + 1]) * context.yfactor;
				print(x + " " + y + " ");
			}

		} else if ("x".equalsIgnoreCase(call[0])
				|| "close".equalsIgnoreCase(call[0])) {
			print("Z ");
		} else if (!("ns".equalsIgnoreCase(call[0])
				|| "nostroke".equalsIgnoreCase(call[0])
				|| "nf".equalsIgnoreCase(call[0]) || "nofill"
				.equalsIgnoreCase(call[0]))) {
			System.out.println("Unknown command: " + call[0]);
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
		String[] formula = autoshape.formulas[i];
		double operand1 = translate(formula[1]);
		double operand2 = (formula.length > 2 ? translate(formula[2]) : 0);
		double operand3 = (formula.length > 3 ? translate(formula[3]) : 0);
		;

		if ("sum".equalsIgnoreCase(formula[0])) {
			return operand1 + operand2 - operand3;
		}
		if ("product".equalsIgnoreCase(formula[0])
				|| "prod".equalsIgnoreCase(formula[0])) {
			return operand1 * operand2 / operand3;
		}
		if ("mid".equalsIgnoreCase(formula[0])) {
			return (operand1 + operand2) / 2;
		}
		if ("absolute".equalsIgnoreCase(formula[0])
				|| "abs".equalsIgnoreCase(formula[0])) {
			return Math.abs(operand1);
		}
		if ("min".equalsIgnoreCase(formula[0])) {
			return Math.min(operand1, operand2);
		}
		if ("max".equalsIgnoreCase(formula[0])) {
			return Math.max(operand1, operand2);
		}
		if ("if".equalsIgnoreCase(formula[0])) {
			return (operand1 > 0 ? operand2 : operand3);
		}
		if ("sqrt".equalsIgnoreCase(formula[0])) {
			return Math.sqrt(operand1);
		}
		if ("mod".equalsIgnoreCase(formula[0])) {
			return Math.sqrt(operand1 * operand1 + operand2 * operand2
					+ operand3 * operand3);
		}
		if ("sin".equalsIgnoreCase(formula[0])) {
			return operand1 * Math.sin(operand2);
		}
		if ("cos".equalsIgnoreCase(formula[0])) {
			return operand1 * Math.cos(operand2);
		}
		if ("atan2".equalsIgnoreCase(formula[0])) {
			return Math.atan2(operand2, operand1);
		}
		if ("sinatan2".equalsIgnoreCase(formula[0])) {
			return operand1 * Math.sin(Math.atan2(operand3, operand2));
		}
		if ("cosatan2".equalsIgnoreCase(formula[0])) {
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
	 * @param record
	 *            - a spContainer record
	 */
	private void parseGroupShape(EscherContainerRecord record) {
		// get subimage
		EscherSpgrRecord spgr = (EscherSpgrRecord) record
				.getChildById((short) 0xF009);
		int x = spgr.getRectX1();
		int y = spgr.getRectY1();
		int w = spgr.getRectX2() - x;
		int h = spgr.getRectY2() - y;

		// draw children
		// TODO draw according to z-index
		open("<g>");
		for (EscherRecord child : record.getChildRecords()) {
			if (child.getRecordId() == (short) 0xF003) {
				parseGroupShape((EscherContainerRecord) child);
			}
			if (child.getRecordId() == (short) 0xF004) {
				parseAtomShape(new ShapeContext(w, h),
						(EscherContainerRecord) child);
			}
		}
		open("</g>");
	}

	static class ShapeContext {

		public final double xfactor, yfactor;

		public ShapeContext(double width, double height) {
			super();
			xfactor = width / SPACE_SIZE;
			yfactor = height / SPACE_SIZE;
		}

	}

}
