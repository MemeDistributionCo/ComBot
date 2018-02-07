package com.mdc.combot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import javax.security.auth.login.LoginException;

import com.mdc.combot.command.Command;
import com.mdc.combot.plugin.BotPlugin;
import com.mdc.combot.util.Util;
import com.mdc.combot.util.exception.BotAlreadyRunningException;
import com.mdc.combot.util.exception.PluginTXTNotFoundException;

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
	private final String version = "0.1.0";
	private JDA jdaInstance;
	private Set<Command> commands;
	private Set<BotPlugin> plugins;
	
	/**
	 * Initialize a new ComBot with the provided token. The bot still needs to be logged in.
	 * @param botToken The bot user token
	 */
	public ComBot(String botToken) {
		this.botToken = botToken;
		jdaInstance = null;
		commands = new HashSet<Command>();
		plugins = new HashSet<BotPlugin>();
	}
	
	/**
	 * Get the command prefix used for this bot
	 * @return The prefix as a String
	 */
	public String getCommandPrefix() {
		return "--";
		//TODO Configurable
	}
	
	public void registerCommand(Command c) {
		this.commands.add(c);
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
	public void start() throws LoginException,IllegalArgumentException, InterruptedException, RateLimitedException, BotAlreadyRunningException {
		login();
		loadPlugins();
	}
	
	protected void loadPlugins() {
		File f = new File(Util.PLUGIN_DIR_PATH);
		if(!f.exists()) {
			f.mkdirs();
		}
		for(String fileName : f.list(new FilenameFilter() {@Override public boolean accept(File dir, String name) { return name.endsWith(".jar");}})) {
			File possiblePlugin = new File(f.getPath() + File.separatorChar + fileName);
			try {
				JarFile jar = new JarFile(possiblePlugin);
				ZipEntry pluginTxt = jar.getEntry("plugin.txt");
				/*
				 * If plugin.txt doesn't exist, this won't work
				 */
				if(pluginTxt == null) {
					jar.close();
					throw new PluginTXTNotFoundException(fileName);
				}
				BufferedReader br = new BufferedReader(new InputStreamReader(jar.getInputStream(pluginTxt)));
				String line = br.readLine();
				if(line.contains("main:")) {
					line = line.replace("main:", "").trim();
					URL loc = possiblePlugin.toURI().toURL();
					URLClassLoader loader = new URLClassLoader(new URL[] {loc});
					Class<?> loadedClass = loader.loadClass(line);
					for(Class<?> i : loadedClass.getInterfaces()) {
						if(i.getName().equals("com.mdc.combot.plugin.BotPlugin")) {
							Object instance = loadedClass.newInstance();
							if(instance instanceof BotPlugin) {
								BotPlugin pl = (BotPlugin)instance;
								//Load the rest of the classes
								plugins.add(pl);
								Enumeration<JarEntry> entries = jar.entries();
								while(entries.hasMoreElements()) {
									JarEntry entry = entries.nextElement();
									if(entry.getName().endsWith(".class")) {
										loader.loadClass(entry.getName().replace(".class","").replace("/",".").replace("\\", ".").replace(":", "."));
									}
								}
							}
						}
					}
					loader.close();
				}
				br.close();
				jar.close();
			} catch (IOException | PluginTXTNotFoundException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		for(BotPlugin pl : plugins) {
			pl.enable();
		}
	}
	
	protected void unloadPlugins() {
		for(BotPlugin pl : plugins) {
			pl.disable();
		}
	}
	
	/**
	 * Shut down the bot. Unload plugins, logout.
	 */
	public void shutdown() {
		unloadPlugins();
		jdaInstance.shutdown();
		jdaInstance = null;
		ComBot.setBot(null);
	}
	
	
	private void login() throws LoginException,IllegalArgumentException, InterruptedException, RateLimitedException, BotAlreadyRunningException {
		jdaInstance = new JDABuilder(AccountType.BOT).setToken(botToken).buildBlocking();
		if(jdaInstance != null) {
			jdaInstance.addEventListener(new DefaultCommandListener(this));
			if(!ComBot.setBot(this)) {
				throw new BotAlreadyRunningException();
			}
		}
	}
	
	/**
	 * Get the commands registered to this bot
	 * @return A collection of commands
	 */
	public Collection<Command> getRegisteredCommands() {
		return this.commands;
	}
	
	/**
	 * Check if this bot is alive (jda!=null)
	 * @return Whether this bot is alive
	 */
	public boolean isAlive() {
		return this.jdaInstance != null;
	}
	
	
	private static ComBot runningBot;
	
	/**
	 * Set the current bot. If the bot is already set, this will fail. The current bot must be shutdown first.
	 * @param b The bot to set
	 * @return True, if the bot could be set
	 */
	public static boolean setBot(ComBot b) {
		if(runningBot == null) {
			runningBot = b;
			return true;
		} else {
			if(!runningBot.isAlive()) {
				runningBot = b;
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Get the current ComBot running. If there is no bot running, it will return null.
	 * @return A bot instance or null
	 */
	public static ComBot getBot() {
		return runningBot;
	}
}
