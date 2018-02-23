package com.mdc.combot.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import net.dv8tion.jda.core.entities.Guild;

public class Config {

	/*
	 * It's kind of dumb to wrap a map in a class
	 */
	
	private Map<String,String> configMap;
	
	private Config(Map<String,String> map) {
		configMap = map;
	}
	
	
	
	public String get(String s) {
		return configMap.get(s);
	}
	
	
	private static void createDefaultConfigFile(File loc) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(loc));
			InputStream is = Config.class.getResourceAsStream("/files/defaultconfig.txt");
			Scanner s = new Scanner(is);
			while(s.hasNextLine()) {
				bw.write(s.nextLine()+'\n');
			}
			bw.flush();
			s.close();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Config getConfigurationInstance() {
		String path = Util.BOT_SETTINGS_PATH;
		File configFile = new File(path + File.separatorChar + "config.txt");
		if(!configFile.exists()) {
			try {
				configFile.createNewFile();
				createDefaultConfigFile(configFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return readConfigFromFile(configFile);
	}


	private static Config readConfigFromFile(File f) {
		try {
			Map<String,String> configMap = new HashMap<String,String>();
			
			Scanner s = new Scanner(new FileInputStream(f));
			while(s.hasNextLine()) {
				String ln = s.nextLine();
				if(ln.trim().startsWith("#") || ln.trim().equals("")) {
					continue;
				}
				String key = ln.substring(0, ln.indexOf(':')).replace(" ", "");
				String value = ln.substring(ln.indexOf(':')+1);
				configMap.put(key, value);
			}
			s.close();
			return new Config(configMap);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error loading " + f.getName() + " as config");
			return null;
		}
	}
	
	public static Config getConfigForGuild(Guild g) {
		if(!Config.guildHasConfig(g)) {
			return null;
		}
		File f = new File(Util.BOT_SETTINGS_PATH + File.separatorChar + "multiserver-config" + File.separatorChar + g.getId() + ".txt");
		return readConfigFromFile(f);
	}
	
	/**
	 * Checks whether this guilds <code>guildid.txt</code> config file exists in <code>./multiserver-config/</code>
	 * @param g The guild to check
	 * @return True, if it exists
	 */
	public static boolean guildHasConfig(Guild g) {
		File f = new File(Util.BOT_SETTINGS_PATH + File.separatorChar + "multiserver-config" + File.separatorChar + g.getId() + ".txt");
		return f.exists() && f.isFile();
	}
	
	/**
	 * Create a configuration file for a guild in <code>./multiserver-config/guildid.txt</code>. Overwrites existing file.
	 * @param g The guild
	 */
	public static void createConfigForGuild(Guild g) {
		File f = new File(Util.BOT_SETTINGS_PATH + File.separatorChar + "multiserver-config" + File.separatorChar + g.getId() + ".txt");
		if(!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		createDefaultConfigFile(f);
	}

	public static Map<String, Config> getMulticonfigMap() {
		String path = Util.BOT_SETTINGS_PATH;
		File configDirectory = new File(path + File.separatorChar + "multiserver-config");
		if(!configDirectory.exists()) {
			configDirectory.mkdirs();
			return new HashMap<String,Config>();
		}
		/*
		 * Y'all hoes best not mess with my nice Christian f i l e  f o r m a t t i n g
		 */
		Map<String,Config> multiMap = new HashMap<String,Config>();
		for(File confFile : configDirectory.listFiles()) {
			long guildId = Long.parseLong(confFile.getName().replace(".txt", ""));
			Config guildConfig = readConfigFromFile(confFile);
			if(guildConfig!=null) {
				multiMap.put(""+guildId, guildConfig);
			}
		}
				
		
		return multiMap;
	}
	
	
}
