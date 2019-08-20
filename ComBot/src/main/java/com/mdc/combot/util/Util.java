package com.mdc.combot.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mdc.combot.ComBot;
import com.mdc.combot.command.Command;
import com.mdc.combot.util.exception.TokenNotFoundException;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;

/**
 * Utility class for bot related things
 * @author xDestx
 *
 */
public class Util {
	/**
	 * The Bot data folder path
	 */
	public final static String BOT_PATH = System.getProperty("user.home") + File.separatorChar + "ComBot";
	/**
	 * The bot settings folder path
	 */
	public final static String BOT_SETTINGS_PATH = BOT_PATH + File.separatorChar + "settings";
	/**
	 * The bot token path
	 */
	public final static String TOKEN_FILE_PATH = BOT_SETTINGS_PATH + File.separatorChar + "token.txt";
	
	public final static String PLUGIN_DIR_PATH = BOT_PATH + File.separatorChar + "plugins";
	
	
	/**
	 * Join together strings in an array while adding a space in between each element
	 * @param s String array
	 * @param startingIndex start at specified position in array (0 for all strings in array)
	 * @return A full string
	 */
	public static String joinStrings(String[] s, int startingIndex) {
		if(startingIndex > s.length-1)
			return "";
		String fin ="";
		for (int i = startingIndex; i < s.length; i++) {
			fin+=s[i] + (i == s.length-1 ? "":" ");
		}
		return fin;
	}

	/**
	 * Check whether two users are the same
	 * @param u1 User 1
	 * @param u2 User 2
	 * @return Whether they are the same
	 */
	public static boolean sameUser(User u1, User u2) {
		long id1 = u1.getIdLong();
		long id2 = u2.getIdLong();
		return id1 == id2;
	}
	
	/**
	 * Random value, inclusive on both ends
	 * @param begin beginning value
	 * @param end ending value
	 * @return the random value
	 */
	public static int randVal(int begin, int end) {
		return (int)(Math.random() * (end-begin+1)) + begin;
	}
	
	/**
	 * Attempts to read the token from the {@link com.mdc.combot.util.Util#TOKEN_FILE_PATH Token File}. If the file didn't exist previously, it is created and a {@link TokenNotFoundException} is thrown.
	 * @return A String of the token used for the bot
	 * @throws IOException Failed IO action
	 * @throws TokenNotFoundException Token file does not exist
	 */
	public static String readToken() throws IOException, TokenNotFoundException {
		File tokenFolder = new File(Util.BOT_SETTINGS_PATH);
		if(!tokenFolder.exists()) {
			tokenFolder.mkdirs();
		}
		File tokenFile = new File(Util.TOKEN_FILE_PATH);
		if(!tokenFile.exists()) {
			tokenFile.createNewFile();
			FileWriter fw = new FileWriter(tokenFile);
			fw.write("token: ");
			fw.close();
			//Token never existed
			throw new TokenNotFoundException("We had to generate a new token file for you. You cannot run a bot without a token.", Util.TOKEN_FILE_PATH);
		}
		
		BufferedReader fr = new BufferedReader(new FileReader(tokenFile));
		String token = fr.readLine();
		fr.close();
		token = token.replace("token:", "");
		//token.replace(" ", "");
		token = token.trim();
	
		return token;
	}
	
	/**
	 * Adds all roles with the name "rolename" to the Member mem.
	 * Submit action after method completed
	 * @param g The guild to check for role, member, etc.
	 * @param mem The member to add role to
	 * @param roleName The name of the role(s) to add
	 * @return A rest action which needs to be <em>completed</em>
	 */
	public static AuditableRestAction<Void> addRolesToMember(Guild g, Member mem, String roleName) {
		List<Role> r = g.getRolesByName(roleName, true);
		List<Role> memRoles = new ArrayList<Role>(mem.getRoles());
		r.addAll(memRoles);
		return g.modifyMemberRoles(mem, r);
	}
	
	/**
	 * Removes all roles with the name "rolename" to the Member mem
	 * @param g The guild to check for role, member, etc.
	 * @param mem The member to add role to
	 * @param roleName The name of the role(s) to add
	 * @return A rest action which needs to me <em>completed</em>
	 */
	public static AuditableRestAction<Void> removeRolesFromMember(Guild g, Member mem, String roleName) {
		List<Role> r = g.getRolesByName(roleName, true);
		List<Role> memRoles = new ArrayList<>(mem.getRoles());
		memRoles.removeAll(r);
		return g.modifyMemberRoles(mem, memRoles);
	}

	
	/**
	 * Get the provided Users currently displayed name (Nickname).
	 * @param g The guild for the user
	 * @param u The user
	 * @return The User's display name
	 */
	public static String getUserDisplayName(User u, Guild g) {
		return getUserDisplayName(g.getMember(u));
	}
	
	/**
	 * Retrieve the member's nickname ({@link Member#getNickname()}
	 * @param m The member
	 * @return Their nick name as a String
	 */
	public static String getUserDisplayName(Member m) {
		return m.getNickname();
	}

	/**
	 * Check whether a user has a role
	 * @param u The user
	 * @param role The role name
	 * @param g The server instance
	 * @return true, if they have it
	 */

	public static boolean userHasRole(User u, String role, Guild g) {
		Member m = g.getMember(u);
		for(Role r : m.getRoles()) {
			if(r.getName().equalsIgnoreCase(role)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Turn string array into a map, same format as if from file
	 * @param lines Lines
	 * @return
	 */
	public static Map<String,String> mapFromString(String[] lines) {
		Map<String,String> configMap = new HashMap<String,String>();
		
		for(String s : lines) {
			String ln = s;
			if(ln.trim().startsWith("#") || ln.trim().equals("")) {
				continue;
			}
			String key = ln.substring(0, ln.indexOf(':')).replace(" ", "");
			String value = ln.substring(ln.indexOf(':')+1).trim();
			configMap.put(key, value);
		}
		
		return configMap;
	}
	
	/**
	 * Get the message from a command, excluding the command prefix and label
	 * @param g The which the command was called from
	 * @param cmdLabel The command label
	 * @param rawMsg The raw msg content
	 * @return The raw msg without prefix or label
	 */
	public static String getMsgFromCommand(Guild g, String cmdLabel, String rawMsg) {
		return rawMsg.replace(ComBot.getBot().getCommandPrefix(g) + cmdLabel, "").trim();
	}
	
	/**
	 * Get the message from a command, excluding the command prefix and label
	 * @param c The command object
	 * @param e The message event
	 * @return The raw msg without prefix or label
	 */
	public static String getMsgFromCommand(Command c, MessageReceivedEvent e) {
		return Util.getMsgFromCommand(e.getGuild(), c.getLabel(), e.getMessage().getContentRaw());
	}
	
	/**
	 * Get the command arguments from a given command
	 * @param c The command object
	 * @param e The message received
	 * @return Command arguments (separated by space)
	 */
	public static String[] getArgsFromCommand(Command c, MessageReceivedEvent e) {
		return Util.getMsgFromCommand(c, e).split(" ");
	}
	
	
	/**
	 * Get a map from a file following the simple config combot style.
	 * Lines that start wtih a # are ignored
	 * Any other line is scanned as a key value pair with a : as a divider.
	 * @param f The file to load
	 * @return A map, if possible.
	 */
	public static Map<String,String> mapFromFile(File f) {
		try {
			Map<String,String> configMap = new HashMap<String,String>();
			
			Scanner s = new Scanner(new FileInputStream(f));
			while(s.hasNextLine()) {
				String ln = s.nextLine();
				if(ln.trim().startsWith("#") || ln.trim().equals("")) {
					continue;
				}
				String key = ln.substring(0, ln.indexOf(':')).replace(" ", "");
				String value = ln.substring(ln.indexOf(':')+1).trim();
				configMap.put(key, value);
			}
			s.close();
			return configMap;
		} catch (IOException e) {
			e.printStackTrace();
			Logger.getLogger("ComBot").log(Level.WARNING, "[ComBot Util] Error loading " + f.getName() + ".");
			return null;
		}
	}
}
