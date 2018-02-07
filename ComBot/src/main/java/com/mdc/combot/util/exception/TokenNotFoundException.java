package com.mdc.combot.util.exception;

/**
 * Thrown when a token file is not found.
 * @author xDest
 *
 */
public class TokenNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6187436825931119167L;

	private final String filePath;
	
	/**
	 * Accepts on argument for the file path and one for the message
	 * @param msg Error message
	 * @param filePath Token file path
	 */
	public TokenNotFoundException(String msg, String filePath) {
		super(msg);
		this.filePath = filePath;
	}
	
	/**
	 * Get the token path
	 * @return The path
	 */
	public final String getTokenPath() {
		return this.filePath;
	}
}
