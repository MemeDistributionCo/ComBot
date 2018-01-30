package com.mdc.combot;

import java.util.Set;

import javax.security.auth.login.LoginException;

import com.mdc.combot.event.Event;
import com.mdc.combot.listener.Listener;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

/**
 * ComBot class. The purpose of the ComBot, or <em>Community Bot</em>, is to be able to combine features from other bots into a single instance.
 * @author xDestx
 *
 */
public class ComBot {

	private final String botToken;
	private final String version = "0.0.1";
	private JDA jdaInstance;
	private Set<Listener> listeners;
	
	/**
	 * Initialize a new ComBot with the provided token. The bot still needs to be logged in.
	 * @param botToken The bot user token
	 */
	public ComBot(String botToken) {
		this.botToken = botToken;
		jdaInstance = null;
	}
	
	/**
	 * Invoke an event for this bot to process.
	 * @param e The event for this bot
	 */
	public void invokeEvent(Event e) {
		//Notify listeners
		//Enact event
		//TODO Listeners should be done first...
	}
	
	/**
	 * Get the token used for this bot
	 * @return The bot token
	 */
	public String getBotToken() {
		return this.botToken;
	}
	
	/**
	 * Get the version of the ComBot
	 * @return The version of this ComBot instance
	 */
	public String getVersion() {
		return this.version;
	}
	
	/**
	 * Get the JDA instance for this bot. Will return null if uninitialized.
	 * @return The JDA instance this bot is using, or null.
	 */
	public JDA getJDA() {
		return this.jdaInstance;
	}
	
	public void login() throws LoginException,IllegalArgumentException, InterruptedException, RateLimitedException {
		jdaInstance = new JDABuilder(AccountType.BOT).setToken(botToken).buildBlocking();
		
	}
	
	
}
