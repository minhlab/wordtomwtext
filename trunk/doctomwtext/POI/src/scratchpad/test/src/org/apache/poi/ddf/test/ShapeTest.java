package org.apache.poi.ddf.test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.model.ShapesTable;
import org.apache.poi.hwpf.usermodel.Shape;

public class ShapeTest {
	public static void main(String[] args) throws FileNotFoundException,
			IOException {
		HWPFDocument doc = new HWPFDocument(new FileInputStream(
				"test/doc/line.doc"));
		ShapesTable shapesTable = doc.getShapesTable();
		Shape shape = (Shape) shapesTable.getAllShapes().get(0);
		System.out.println(shape.getTop());
		System.out.println(shape.getLeft());
		System.out.println(shape.getBottom());
		System.out.println(shape.getRight());
		
	}
}
