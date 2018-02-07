package com.mdc.combot.util.exception;

public class BotAlreadyRunningException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8177647787866865880L;

	public BotAlreadyRunningException() {
		super("An instance of a ComBot is already running. Terminate it before starting a new one. Call ComBot.getBot() to retrieve it.");
	}
	
}
