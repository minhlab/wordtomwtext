package org.apache.poi.ddf.converter;

public class Autoshape {

	public int id;

	public String name;

	public String internalName;

	public String[][] calls;

	public String[][] formulas;

	public Autoshape() {
	}
	
	public Autoshape(int id, String name, String internalName,
			String[][] calls, String[][] formulas) {
		super();
		this.id = id;
		this.name = name;
		this.internalName = internalName;
		this.calls = calls;
		this.formulas = formulas;
	}

	@Override
	public String toString() {
		return name + " (" + internalName + ", " + id + ")";
	}

}
