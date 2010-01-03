package org.apache.poi.ddf.converter.autoshape;

public class Call {

	private String command;
	
	private String[] params;

	public Call(String command, String[] params) {
		super();
		this.command = command;
		this.params = params;
	}

	public String getCommand() {
		return command;
	}

	public String[] getParams() {
		return params;
	}
	
	
}
