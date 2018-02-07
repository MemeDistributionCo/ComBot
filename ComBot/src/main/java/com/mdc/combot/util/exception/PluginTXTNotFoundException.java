package com.mdc.combot.util.exception;

public class PluginTXTNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4797006417979883122L;
	private final String fileName;
	
	public PluginTXTNotFoundException(String fileName) {
		super("Couldn't find plugin.txt in " + fileName);
		this.fileName = fileName;
	}

	
	public String getFileName() {
		return this.fileName;
	}
	
}