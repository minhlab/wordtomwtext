package org.apache.poi.ddf.converter.autoshape;

public class Autoshape {

	private Call[] call;
	
	private Formula[] formulas;

	public Autoshape(Call[] commands, Formula[] formulas) {
		super();
		this.call = commands;
		this.formulas = formulas;
	}

	public Formula[] getFormulas() {
		return formulas;
	}

	public Call[] getCalls() {
		return call;
	}
	
}
