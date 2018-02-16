package com.mdc.combot.permissions;

import net.dv8tion.jda.core.entities.Member;

/**
 * An interface for permission management. Note: Permissions are only worth what you make them. If you don't check for permissions, they might as well not exist. The purpose of this class
 * is to manage gathering and assigning who has what permissions, not to do the actual moderation.
 * @author xDest
 *
 */
public interface PermissionsInstance {

	/**
	 * Check whether the specified member has permission for the following action
	 * @param perm The permission name
	 * @param m The member
	 * @return True, if the member can do the action
	 */
	boolean memberHasPermission(String perm, Member m);
	
}
