package com.mdc.combot;

import java.io.IOException;

import javax.security.auth.login.LoginException;

import com.mdc.combot.ComBot;
import com.mdc.combot.util.Util;
import com.mdc.combot.util.exception.BotAlreadyRunningException;
import com.mdc.combot.util.exception.TokenNotFoundException;

import net.dv8tion.jda.core.exceptions.RateLimitedException;

public class BotLauncher {

	private static ComBot bot;
	private static String token;

	public static void main(String[] args) {
		readToken();
		startBot();
	}
	
	
	public static void startBot() {
		bot = new ComBot(token);
		try {
			bot.start();
		} catch (LoginException | IllegalArgumentException | InterruptedException | RateLimitedException | BotAlreadyRunningException e) {
			e.printStackTrace();
			System.out.println("Unable to log in! Yikes! (Invalid token?)");
		}
	}
	
	public static void readToken() {
		try {
			token = Util.readToken();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Could not read or write token file...");
			System.exit(1);
			return;
		} catch (TokenNotFoundException e) {
			System.out.println(e.getMessage());
			System.out.println("Token file path: " + e.getTokenPath());
			System.exit(1);
			return;
		}
	}
	
	public static void restart() {
		if(bot!=null) {
			bot.shutdown();
			bot = null;
		}
		readToken();
		startBot();
		//I'm nervous that restarting a million times will cause a stack overflow
	}
	
}
