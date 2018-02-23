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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import javax.security.auth.login.LoginException;

import com.mdc.combot.command.Command;
import com.mdc.combot.command.RestartCommand;
import com.mdc.combot.command.ShutdownCommand;
import com.mdc.combot.permissions.DefaultPermissionManager;
import com.mdc.combot.permissions.PermissionsInstance;
import com.mdc.combot.plugin.BotPlugin;
import com.mdc.combot.util.Config;
import com.mdc.combot.util.Util;
import com.mdc.combot.util.exception.BotAlreadyRunningException;
import com.mdc.combot.util.exception.PluginTXTNotFoundException;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

/**
 * ComBot class. The purpose of the ComBot, or <em>Community Bot</em>, is to be
 * able to combine features from other bots into a single instance.
 * 
 * @author xDestx
 *
 */
public class ComBot {

	private final String botToken;
	private final String version = "0.9.2";
	private JDA jdaInstance;
	private Set<Command> commands;
	private Set<BotPlugin> plugins;
	private Config config;
	private Map<String, Config> multiserverConfig;
	private PermissionsInstance perms;
	private boolean multiServer;

	/**
	 * Initialize a new ComBot with the provided token. The bot still needs to be
	 * logged in.
	 * 
	 * @param botToken
	 *            The bot user token
	 */
	public ComBot(String botToken) {
		this.botToken = botToken;
		jdaInstance = null;
		commands = new HashSet<Command>();
		plugins = new HashSet<BotPlugin>();
		this.config = Config.getConfigurationInstance();
		perms = DefaultPermissionManager.getPermissionManager();
		registerDefaultCommands();

		if (this.config.get("enable-multiserver-config") != null
				&& Boolean.parseBoolean(this.config.get("enable-multiserver-config"))) {
			this.multiserverConfig = Config.getMulticonfigMap();
			multiServer = true;
		} else {
			this.multiserverConfig = null;
			multiServer = false;
		}
	}

	/**
	 * Get whether the bot has multiserver config enabled
	 * 
	 * @return True, if so
	 */
	public boolean isMultiServerOn() {
		return this.multiServer;
	}

	/**
	 * Get the config map for guilds
	 * 
	 * @return A map with key of guild id and value of server config
	 */
	public Map<String, Config> getGuildSpecificConfig() {
		return this.multiserverConfig;
	}

	/**
	 * Change the current permissions manager
	 * 
	 * @param p
	 *            The new manager
	 */
	public void setPermissionManager(PermissionsInstance p) {
		this.perms = p;
	}

	/**
	 * Check whether the member has the specified permission.
	 * 
	 * @see PermissionsInstance#memberHasPermission(String,
	 *      net.dv8tion.jda.core.entities.Member)
	 * @param permission
	 *            The permission
	 * @param m
	 *            The member
	 * @return True, if it does.
	 */
	public boolean memberHasPerm(String permission, Member m) {
		return perms.memberHasPermission(permission, m);
	}

	/**
	 * Sends a message to the default text channel. This method will have no effect when multiserver support is turned on
	 * 
	 * @param message
	 *            The message to send
	 */
	@Deprecated
	public void sendMessage(String message) {
		if (!this.multiServer) {
			String chName = config.get("default-text-channel");
			if (!(chName == null || chName.equals(""))) {
				List<TextChannel> channels = jdaInstance.getTextChannelsByName(chName, false);
				if (channels.size() > 0) {
					TextChannel c = channels.get(0);
					MessageBuilder mb = new MessageBuilder();
					mb.append(message);
					c.sendMessage(mb.build()).complete();
				}
			}
		}
	}
	
	/**
	 * Send a message to the default text channel in the specified guild. If the guild is null, this will call {@link #sendMessage(String message)}
	 * @param msg The message
	 * @param g The guild to send to.
	 */
	public void sendMessage(String msg, Guild g) {
		if(g != null) {
			String chName = this.multiserverConfig.get(g.getId()).get("default-text-channel");
			if (!(chName == null || chName.equals(""))) {
				List<TextChannel> channels = jdaInstance.getTextChannelsByName(chName, false);
				if (channels.size() > 0) {
					TextChannel c = channels.get(0);
					MessageBuilder mb = new MessageBuilder();
					mb.append(msg);
					c.sendMessage(mb.build()).complete();
				}
			}
		} else if (!this.multiServer) {
			sendMessage(msg);
		}
	}
	
	/**
	 * Send a message to the default text channel in the specified guild. If the provided id doesn't represent a guild or can't be found, the method will revert to {@link #sendMessage(String)}
	 * @param msg The message to send
	 * @param guildID The guild ID
	 */
	public void sendMessage(String msg, String guildID) {
		Guild toSend = getJDA().getGuildById(guildID);
		sendMessage(msg,toSend);
	}

	private void registerDefaultCommands() {
		RestartCommand restartCmd = new RestartCommand();
		ShutdownCommand shutdownCmd = new ShutdownCommand();

		this.registerCommand(restartCmd);
		this.registerCommand(shutdownCmd);
	}

	/**
	 * Get the command prefix used for this bot.
	 * <strong>Deprecated.</strong>
	 * 
	 * @return The prefix as a String
	 */
	@Deprecated 
	public String getCommandPrefix() {
		if(!this.multiServer) {
			String cmdPrefix;
			cmdPrefix = config.get("command-prefix");
			if (cmdPrefix == null) {
				cmdPrefix = "--";
				// Default
			}
	
			// I don't like how often it will have to look at the map
			return cmdPrefix;
		} else {
			return "--";
		}
	}
	
	/**
	 * Get the command prefix for the specified guild. If null param, will check for default prefix.
	 * @param g The guild
	 * @return The command prefix for this guild
	 */
	public String getCommandPrefix(Guild g) {
		if(g == null) {
			return getCommandPrefix();
		} else {
			String cmdPrefix;
			cmdPrefix = this.multiserverConfig.get(g.getId()).get("command-prefix");
			if (cmdPrefix == null) {
				cmdPrefix = "--";
				// Default
			}
	
			// I don't like how often it will have to look at the map
			return cmdPrefix;
		}
	}
	
	/**
	 * Get the command prefix for the specified guild.
	 * @param guildId The guild specified
	 * @return The prefix
	 */
	public String getCommandPrefix(String guildId) {
		return getCommandPrefix(getJDA().getGuildById(guildId));
	}

	public void registerCommand(Command c) {
		this.commands.add(c);
	}

	/**
	 * Get the token used for this bot
	 * 
	 * @return The bot token
	 */
	public String getBotToken() {
		return this.botToken;
	}

	/**
	 * Get the version of the ComBot
	 * 
	 * @return The version of this ComBot instance
	 */
	public String getVersion() {
		return this.version;
	}

	/**
	 * Get the JDA instance for this bot. Will return null if uninitialized.
	 * 
	 * @return The JDA instance this bot is using, or null.
	 */
	public JDA getJDA() {
		return this.jdaInstance;
	}

	/**
	 * Starts the bot, loads plugins
	 * 
	 * @throws LoginException
	 *             Please
	 * @throws IllegalArgumentException
	 *             Know
	 * @throws InterruptedException
	 *             What
	 * @throws RateLimitedException
	 *             These
	 * @throws BotAlreadyRunningException
	 *             Are
	 */
	public void start() throws LoginException, IllegalArgumentException, InterruptedException, RateLimitedException,
			BotAlreadyRunningException {
		login();
		loadPlugins();
		if(this.multiServer) {
			checkNewGuilds();
			for(String guildId : this.multiserverConfig.keySet()) {
				Config cfg = this.multiserverConfig.get(guildId);
				if(Boolean.parseBoolean(cfg.get("enable-startup-message"))) {
					this.sendMessage(cfg.get("startup-message"), guildId);
				}
			}
		} else {
			if (Boolean.parseBoolean(config.get("enable-startup-message"))) {
				sendMessage(config.get("startup-message"));
			}
		}
	}
	
	private void checkNewGuilds() {
		//Check for config for new guilds the bot may exist on
		for(Guild g : getJDA().getGuilds()) {
			if(!Config.guildHasConfig(g)) {
				Config.createConfigForGuild(g);
				this.multiserverConfig.put(g.getId(), Config.getConfigForGuild(g));
			}
		}
	}

	protected void loadPlugins() {
		File f = new File(Util.PLUGIN_DIR_PATH);
		if (!f.exists()) {
			f.mkdirs();
		}
		for (String fileName : f.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".jar");
			}
		})) {
			File possiblePlugin = new File(f.getPath() + File.separatorChar + fileName);
			try {
				JarFile jar = new JarFile(possiblePlugin);
				ZipEntry pluginTxt = jar.getEntry("plugin.txt");
				/*
				 * If plugin.txt doesn't exist, this won't work
				 */
				if (pluginTxt == null) {
					jar.close();
					throw new PluginTXTNotFoundException(fileName);
				}
				BufferedReader br = new BufferedReader(new InputStreamReader(jar.getInputStream(pluginTxt)));
				String line = br.readLine();
				if (line.contains("main:")) {
					line = line.replace("main:", "").trim();
					URL loc = possiblePlugin.toURI().toURL();
					URLClassLoader loader = new URLClassLoader(new URL[] { loc });
					Class<?> loadedClass = loader.loadClass(line);
					for (Class<?> i : loadedClass.getInterfaces()) {
						if (i.getName().equals("com.mdc.combot.plugin.BotPlugin")) {
							Object instance = loadedClass.newInstance();
							if (instance instanceof BotPlugin) {
								BotPlugin pl = (BotPlugin) instance;
								// Load the rest of the classes
								plugins.add(pl);
								Enumeration<JarEntry> entries = jar.entries();
								while (entries.hasMoreElements()) {
									JarEntry entry = entries.nextElement();
									if (entry.getName().endsWith(".class")) {
										loader.loadClass(entry.getName().replace(".class", "").replace("/", ".")
												.replace("\\", ".").replace(":", "."));
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
		for (BotPlugin pl : plugins) {
			pl.enable();
		}
	}

	protected void unloadPlugins() {
		for (BotPlugin pl : plugins) {
			pl.disable();
		}
	}

	/**
	 * Shut down the bot. Unload plugins, logout.
	 */
	public void shutdown() {
		System.out.println("Shutdown started");
		unloadPlugins();
		jdaInstance.shutdown();
		jdaInstance = null;
		ComBot.setBot(null);
		System.out.println("Shutdown complete");
	}

	private void login() throws LoginException, IllegalArgumentException, InterruptedException, RateLimitedException,
			BotAlreadyRunningException {
		jdaInstance = new JDABuilder(AccountType.BOT).setToken(botToken).buildBlocking();
		if (jdaInstance != null) {
			jdaInstance.addEventListener(new DefaultCommandListener(this));
			if (!ComBot.setBot(this)) {
				throw new BotAlreadyRunningException();
			}
		}
	}

	/**
	 * Get the commands registered to this bot
	 * 
	 * @return A collection of commands
	 */
	public Collection<Command> getRegisteredCommands() {
		return this.commands;
	}

	/**
	 * Check if this bot is alive (jda!=null)
	 * 
	 * @return Whether this bot is alive
	 */
	public boolean isAlive() {
		return this.jdaInstance != null;
	}

	private static ComBot runningBot;

	/**
	 * Set the current bot. If the bot is already set, this will fail. The current
	 * bot must be shutdown first.
	 * 
	 * @param b
	 *            The bot to set
	 * @return True, if the bot could be set
	 */
	public static boolean setBot(ComBot b) {
		if (runningBot == null) {
			runningBot = b;
			return true;
		} else {
			if (!runningBot.isAlive()) {
				runningBot = b;
				return true;
			}
		}
		return false;
	}

	/**
	 * Get the current ComBot running. If there is no bot running, it will return
	 * null.
	 * 
	 * @return A bot instance or null
	 */
	public static ComBot getBot() {
		return runningBot;
	}
}
