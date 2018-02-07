package com.mdc.combot;

import java.io.IOException;

import javax.security.auth.login.LoginException;

import com.mdc.combot.ComBot;
import com.mdc.combot.util.Util;
import com.mdc.combot.util.exception.BotAlreadyRunningException;
import com.mdc.combot.util.exception.TokenNotFoundException;

import net.dv8tion.jda.core.exceptions.RateLimitedException;

public class BotLauncher {

	public static void main(String[] args) {
		String token;
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
		
		ComBot newBot = new ComBot(token);
		
		try {
			newBot.start();
		} catch (LoginException | IllegalArgumentException | InterruptedException | RateLimitedException | BotAlreadyRunningException e) {
			e.printStackTrace();
			System.out.println("Unable to log in! Yikes! (Invalid token?)");
		}
	}
	
}
