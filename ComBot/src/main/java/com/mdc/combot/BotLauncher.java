package com.mdc.combot;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.security.auth.login.LoginException;

import com.mdc.combot.util.Util;
import com.mdc.combot.util.exception.BotAlreadyRunningException;
import com.mdc.combot.util.exception.TokenNotFoundException;

import net.dv8tion.jda.core.exceptions.RateLimitedException;

public class BotLauncher {

	private static ComBot bot;
	private static String token;

	public static void main(String[] args) {
		Logger comBotLogger = Logger.getLogger("ComBot");
		final Calendar nowInstance = Calendar.getInstance();
		comBotLogger.addHandler(new Handler() {

			private Queue<LogRecord> records = new LinkedList<LogRecord>();
			private boolean open = true;
			
			
			@Override
			public void publish(LogRecord record) {
				if(!open) return;
				records.add(record);
			}

			@Override
			public void flush() {
				if(!open) return;
				File f = new File(Util.BOT_PATH + File.separatorChar + "logs" + File.separatorChar + "ComBot_run_log." + nowInstance.get(Calendar.MONTH) + "." + nowInstance.get(Calendar.DAY_OF_MONTH) + "." + nowInstance.get(Calendar.YEAR) + "." + nowInstance.get(Calendar.HOUR_OF_DAY) + "." + nowInstance.get(Calendar.MINUTE) + "." + nowInstance.get(Calendar.SECOND) + ".log");
				try {
					if(!f.getParentFile().exists()) {
						f.getParentFile().mkdirs();
					}
					if(!f.exists()) {
						f.createNewFile();
					}
					PrintWriter pw = new PrintWriter(new FileWriter(f));
					for(LogRecord l : records) {
						pw.printf("%s [%s] [%s] %s\n", new Date(l.getMillis()).toString(), l.getLoggerName(), l.getLevel(), l.getMessage());
					}
					pw.flush();
					pw.close();
				} catch (Exception e) {
					//yeet
					//failed to make log file
					//yikes
				}
			}

			@Override
			public void close() throws SecurityException {
				if(!open) return;
				flush();
				open=false;
			}
			
		});
		readToken();
		startBot();
	}
	
	
	public static void startBot() {
		bot = new ComBot(token);
		try {
			Logger.getLogger("ComBot").info("Starting...");
			bot.start();
		} catch (LoginException | IllegalArgumentException | InterruptedException | RateLimitedException | BotAlreadyRunningException e) {
			e.printStackTrace();
			Logger.getLogger("ComBot").severe("Unable to log in! Yikes! (Invalid token?) " + e.getMessage());
		}
	}
	
	public static void readToken() {
		try {
			token = Util.readToken();
		} catch (IOException e) {
			e.printStackTrace();
			Logger.getLogger("ComBot").severe("Could not read or write token file...");
			System.exit(1);
			return;
		} catch (TokenNotFoundException e) {
			Logger.getLogger("ComBot").severe(e.getMessage());
			Logger.getLogger("ComBot").info("\nPLACE TOKEN IN THIS FILE----\n\nToken file path: " + e.getTokenPath());
			System.exit(1);
			return;
		}
	}
	
	public static void restart() {
		Logger.getLogger("ComBot").info("Intiated restart");
		if(bot!=null) {
			bot.shutdown();
			Logger.getLogger("ComBot").info("Bot shutdown");
			bot = null;
		}
		readToken();
		startBot();
		//I'm nervous that restarting a million times will cause a stack overflow
	}
	
}
