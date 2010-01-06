package org.apache.poi.ddf.test;

import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

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
		
		Object obj;
		while ( (obj = in.read()) != null ) {
			ShapeSpec spec = (ShapeSpec) obj;
			Autoshape shape = new Autoshape(spec.id, spec.name,
					spec.internalName, parseCalls(spec.path),
					parseFormulas(spec.formulas));
			out.write(shape);
			System.out.println(shape);
		}
		
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
		String[] parr = path.split("\\s*(?=@|[a-zA-Z])|\\s+");
		int start = 0;
		for (int i = 1; i < parr.length; i++) {
			parr[i] = parr[i].trim();
			if (parr[i].length() <= 0) {
				continue;
			}
			if (Character.isLetter(parr[i].charAt(0))) {
				callList.add(extract(parr, start, i));
				start = i;
			}
		}
		callList.add(extract(parr, start, parr.length));
		String[][] callArr = new String[callList.size()][];
		callList.toArray(callArr);
		return callArr;
	}

	private static String[] extract(String[] arr, int start, int end) {
		String[] ext = new String[end - start];
		System.arraycopy(arr, start, ext, 0, end - start);
		return ext;
	}

}
