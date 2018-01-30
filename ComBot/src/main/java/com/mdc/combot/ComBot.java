package com.mdc.combot;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.security.auth.login.LoginException;

import com.mdc.combot.command.Command;
import com.mdc.combot.event.Event;
import com.mdc.combot.listener.DefaultCommandListener;
import com.mdc.combot.listener.RegisteredEventListener;

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
	//Please only put events in this
	private Map<Class<?>, Set<RegisteredEventListener>> listeners;
	private Set<Command> commands;
	
	/**
	 * Initialize a new ComBot with the provided token. The bot still needs to be logged in.
	 * @param botToken The bot user token
	 */
	public ComBot(String botToken) {
		this.botToken = botToken;
		jdaInstance = null;
		listeners = new HashMap<Class<?>,Set<RegisteredEventListener>>();
		commands = new HashSet<Command>();
	}
	
	public void registerCommand(Command c) {
		this.commands.add(c);
	}
	
	/**
	 * Invoke an event for this bot to process.
	 * @param e The event for this bot
	 */
	public void invokeEvent(Event e) {
		Set<RegisteredEventListener> listenerSet = listeners.get(e.getClass());
		for(RegisteredEventListener l : listenerSet) {
			try {
				l.getMethod().invoke(l.getListenerInstance(), e);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {
				e1.printStackTrace();
			}
		}
		e.enactEvent();
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
	
	/**
	 * Start up the bot. Loads plugins, logs in
	 */
	public void start() throws LoginException,IllegalArgumentException, InterruptedException, RateLimitedException {
		loadPlugins();
		login();
	}
	
	protected void loadPlugins() {
		//TODO This
	}
	
	protected void unloadPlugins() {
		//TODO This
	}
	
	/**
	 * Shut down the bot. Unload plugins, logout.
	 */
	public void shutdown() {
		unloadPlugins();
		
	}
	
	
	protected void login() throws LoginException,IllegalArgumentException, InterruptedException, RateLimitedException {
		jdaInstance = new JDABuilder(AccountType.BOT).setToken(botToken).buildBlocking();
		if(jdaInstance != null) {
			jdaInstance.addEventListener(new DefaultCommandListener(this));
		}
	}
	
	public Collection<Command> getRegisteredCommands() {
		return this.commands;
	}
}
