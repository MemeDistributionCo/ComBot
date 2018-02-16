package com.mdc.combot.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

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
		/*
		 * Y'all hoes best not mess with my nice Christian f i l e  f o r m a t t i n g
		 */
		Map<String,String> configMap = new HashMap<String,String>();
		try {
			Scanner s = new Scanner(new FileInputStream(configFile));
			while(s.hasNextLine()) {
				String ln = s.nextLine();
				String key = ln.substring(0, ln.indexOf(':')).replace(" ", "");
				String value = ln.substring(ln.indexOf(':')+1);
				configMap.put(key, value);
			}
			s.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return new Config(configMap);
	}
	
	
}
