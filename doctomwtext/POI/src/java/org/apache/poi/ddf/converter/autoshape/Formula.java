package org.apache.poi.ddf.converter.autoshape;

public class Formula {

	private String operator;
	
	public Formula(String operator, String[] operands) {
		super();
		this.operator = operator;
		this.operands = operands;
	}

	private String[] operands;

	public String getOperator() {
		return operator;
	}

	public String[] getOperands() {
		return operands;
	}
	
	
	
}
