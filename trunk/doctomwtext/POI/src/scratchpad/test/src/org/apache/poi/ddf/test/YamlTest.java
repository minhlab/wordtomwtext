package org.apache.poi.ddf.test;

import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import net.sourceforge.yamlbeans.YamlReader;
import net.sourceforge.yamlbeans.YamlWriter;

import org.apache.poi.ddf.converter.Autoshape;

public class YamlTest {

	public static void main(String[] args) throws Exception {
		YamlReader in = new YamlReader(new InputStreamReader(YamlTest.class
				.getResourceAsStream("shape-spec.yml")));
		in.getConfig().setClassTag("spec", ShapeSpec.class);
		YamlWriter out = new YamlWriter(new FileWriter("test.yml"));
		out.getConfig().setClassTag("shape", Autoshape.class);
		Properties prop = new Properties();
		prop.load(YamlTest.class.getResourceAsStream("id.properties"));

		int counter = 0;
		Object obj;
		while ((obj = in.read()) != null) {
			ShapeSpec spec = (ShapeSpec) obj;
			Autoshape shape = new Autoshape(spec.id, spec.name,
					spec.internalName, parseCalls(spec.path),
					parseFormulas(spec.formulas));
			if (spec.id == 0) {
				String idStr = prop.getProperty(spec.internalName);
				if (idStr == null) {
					System.out.println("Not found: " + spec.internalName);
				} else {
					prop.remove(spec.internalName);
					spec.id = Integer.parseInt(idStr);
				}
			}
			out.write(shape);
//			System.out.println(shape);
			counter++;
		}
		for (Object internalName : prop.keySet()) {
			System.out.println("Not used: " + internalName);
		}
		System.out.println("Total: " + counter);

		in.close();
		out.close();
	}

	private static String[][] parseFormulas(String formulas) {
		if (formulas == null) {
			return new String[0][];
		}
		List<String[]> formulaList = new ArrayList<String[]>();
		String[] farr = formulas.split("\r?\n");
		for (String formula : farr) {
			formula = formula.trim();
			if (formula.length() <= 0) {
				continue;
			}
			String[] parr = formula.split("\\s+");
			formulaList.add(parr);
		}
		String[][] formulaArr = new String[formulaList.size()][];
		formulaList.toArray(formulaArr);
		return formulaArr;
	}

	private static String[][] parseCalls(String path) {
		List<String[]> callList = new ArrayList<String[]>();
		String[] parr = path
				.split("\\s*(?=@|[a-zA-Z])|\\s+|\\s*,\\s*|(?<=[a-zA-Z])(?=\\d)");
		int start = 0;
		for (int i = 0; i < parr.length; i++) {
			parr[i] = parr[i].trim();
			if (parr[i].length() <= 0) {
				continue;
			}
			if (Character.isLetter(parr[i].charAt(0))) {
				callList.add(extract(parr, start, i + 1));
				start = i + 1;
			}
		}
		if (start < parr.length) {
			callList.add(extract(parr, start, parr.length));
		}
		String[][] callArr = new String[callList.size()][];
		callList.toArray(callArr);
		return callArr;
	}

	private static String[] extract(String[] arr, int start, int end) {
		List<String> partList = new ArrayList<String>();
		for (int i = start; i < end; i++) {
			arr[i] = arr[i].trim();
			if (arr[i].length() <= 0) {
				continue;
			}
			partList.add(arr[i]);
		}
		String[] partArr = new String[partList.size()];
		partList.toArray(partArr);
		return partArr;
	}

}
