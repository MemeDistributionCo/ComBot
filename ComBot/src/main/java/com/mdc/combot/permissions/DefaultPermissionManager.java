package com.mdc.combot.permissions;

import java.io.BufferedWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mdc.combot.util.Util;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;

public class DefaultPermissionManager implements PermissionsInstance {

	private Map<String,Set<String>> rolePermissions;
	private Map<String,Set<String>> userIdPermissions;
	private Set<String> everyonePermissions;
	
	private DefaultPermissionManager(JSONObject j) {
		rolePermissions = new HashMap<String,Set<String>>();
		userIdPermissions = new HashMap<String,Set<String>>();
		JSONArray roleArray = (JSONArray)j.get("roles");
		for(int i = 0; i < roleArray.length(); i++) {
			JSONObject rolePerms = (JSONObject)roleArray.get(i);
			String roleName = rolePerms.getString("roleName");
			JSONArray permissions = (JSONArray)rolePerms.get("permissions");
			Set<String> perms = new HashSet<String>();
			for(int k = 0; k < permissions.length(); k++) {
				perms.add(permissions.getString(k));
			}
			rolePermissions.put(roleName, perms);
		}
		
		JSONArray userArray = (JSONArray)j.get("users");
		for(int i = 0; i < userArray.length(); i++) {
			JSONObject userPerms = (JSONObject)userArray.get(i);
			String userId = userPerms.getString("userId");
			JSONArray permissions = (JSONArray)userPerms.get("permissions");
			Set<String> perms = new HashSet<String>();
			for(int k = 0; k < permissions.length(); k++) {
				perms.add(permissions.getString(k));
			}
			userIdPermissions.put(userId, perms);
		}
		
		try {
			JSONArray everyoneArray = (JSONArray)j.get("everyone");
			Set<String> perms = new HashSet<String>();
			for(int i = 0; i < everyoneArray.length(); i++) {
				perms.add(everyoneArray.getString(i));
			}
			everyonePermissions = perms;
		} catch (JSONException e) {
			//Missing @everyone
			e.printStackTrace();
			everyonePermissions = new HashSet<String>();
			System.out.println("You seem to be missing permissions for @everyone. This isn't a problem, but to avoid this message in the future just add an empty 'everyone' array on the same level as 'roles' and 'users'.");
		}
		
	}
	
	@Override
	public boolean memberHasPermission(String perm, Member m) {
		if(userIdPermissions.get(m.getUser().getId()) != null) {
			if(userIdPermissions.get(m.getUser().getId()).contains(perm) || userIdPermissions.get(m.getUser().getId()).contains("*")) {
				return true;
			} else {
				if(evaluatePermission(perm,userIdPermissions.get(m.getUser().getId()))) {
					return true;
				}
			}
		}
		
		for(Role userRole : m.getRoles()) {
			if(!rolePermissions.containsKey(userRole.getName())) {
				continue;
			}
			if(rolePermissions.get(userRole.getName()).contains(perm) || rolePermissions.get(userRole.getName()).contains("*")) {
				return true;
			} else {
				if(evaluatePermission(perm, rolePermissions.get(userRole.getName()))) {
					return true;
				}
			}
		}
		
		if(everyonePermissions.contains(perm) || everyonePermissions.contains("*")) {
			return true;
		} else {
			for(String checkedPermission : everyonePermissions) {
				if(evaluatePermission(perm, checkedPermission)) {
					return true;
				}
			}
		}

		return false;
	}
	
	private boolean evaluatePermission(String assignedPermission, Set<String> givenPermissions) {
		String[] permSet = assignedPermission.split("\\.");
		String permSub = "";
		Set<String> rolePerms = givenPermissions;
		for(String s : permSet) {
			permSub += s;
			if(rolePerms.contains(permSub+".*")) {
				return true;
			}
		}
		return false;
	}
	
	private boolean evaluatePermission(String assignedPermission, String givenPermission) {
		String[] permSet = assignedPermission.split("\\.");
		String permSub = "";
		for(String s : permSet) {
			permSub+=s;
			if(givenPermission.equalsIgnoreCase(permSub+".*")) {
				return true;
			}
		}
		return false;
	}
	
	
	

	private static void createDefaultPermissionFile(File loc) {
		Scanner s = new Scanner(DefaultPermissionManager.class.getResourceAsStream("/files/defaultpermissions.json"));
		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new FileWriter(loc));
			while(s.hasNextLine()) {
				bw.write(s.nextLine()+"\n");
			}
			bw.flush();
			s.close();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Map of guild ids and their associated permissions.
	 * @return A map containing all guild permissions located in <code>settings/multiserver-permissions</code>
	 */
	public static Map<String,PermissionsInstance> getPermissionMap() {
		String path = Util.BOT_SETTINGS_PATH;
		File permissionsDirectory = new File(path + File.separatorChar + "multiserver-permissions");
		if(!permissionsDirectory.exists()) {
			permissionsDirectory.mkdirs();
			
			return new HashMap<String,PermissionsInstance>();
		}

		Map<String,PermissionsInstance> multiMap = new HashMap<String,PermissionsInstance>();
		for(File confFile : permissionsDirectory.listFiles()) {
			if(!confFile.getName().endsWith(".json")) {
				continue;
			}
			long guildId = Long.parseLong(confFile.getName().replace(".json", ""));
			PermissionsInstance permInstance = readPermissionsFile(confFile);
			if(permInstance!=null) {
				multiMap.put(""+guildId, permInstance);
			}
		}
				
		return multiMap;
	}
	
	private static PermissionsInstance readPermissionsFile(File permFile) {
		if(!permFile.exists()) {
			try {
				permFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
			createDefaultPermissionFile(permFile);
		}
		Scanner s;
		String str = "";
		try {
			s = new Scanner(new FileInputStream(permFile));
			while(s.hasNext()) {
				str+=s.next();
			}
			s.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			return null;
		}
		PermissionsInstance pi;
		try {
			JSONObject jobj = new JSONObject(str);
			pi = new DefaultPermissionManager(jobj);
		} catch (JSONException e) {
			e.printStackTrace();
			pi = null;
		}
		return pi;
	}
	
	public static PermissionsInstance getPermissionManager() {
		String path = Util.BOT_SETTINGS_PATH;
		File permFile = new File(path + File.separatorChar + "permissions.json");
		return readPermissionsFile(permFile);
	}

	public static PermissionsInstance getPermissionForGuild(String id) {
		return readPermissionsFile(new File(Util.BOT_SETTINGS_PATH + File.separatorChar + "multiserver-permissions" + File.separatorChar + id + ".json"));
		
	}
}
