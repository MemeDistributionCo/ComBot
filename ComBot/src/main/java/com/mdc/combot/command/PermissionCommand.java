package com.mdc.combot.command;

import com.mdc.combot.ComBot;
import com.mdc.combot.permissions.DefaultPermissionManager;
import com.mdc.combot.util.Util;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class PermissionCommand implements Command {

	@Override
	public String getLabel() {
		return "perm";
	}

	@Override
	public void called(ComBot bot, MessageReceivedEvent e) {
		if(bot.memberHasPerm("bot.permission", e.getMember())) {
			//perm <add/remove> <role/user/everyone> <local/global> [role/@user] <perm>
			String[] args = Util.getArgsFromCommand(this, e);
			String action = args[0];
			String target = args[1];
			String level = args[2];
			if(args.length == 4) {
				//Everyone
				String perm = args[3];
				if(action.equalsIgnoreCase("add")) {
					//this might take more work than I was expecting whoops
					if(level.equalsIgnoreCase("local")) {
						if(target.equalsIgnoreCase("role")) {
							
						} else if (target.equalsIgnoreCase("user")) {
							
						} else {
							
						}
					} else if (level.equalsIgnoreCase("global")) {
						if(target.equalsIgnoreCase("role")) {
							//
						} else if (target.equalsIgnoreCase("user")) {
							
						} else {
							
						}
					} else {
						e.getTextChannel().sendMessage("Invalid syntax, only allowed perm levels are `local` and `global`").queue();
					}
				} else if (action.equalsIgnoreCase("remove")) {
					
				} else {
					e.getTextChannel().sendMessage("Invalid syntax. `perm <add/remove> <role/user/everyone> <local/global> [role/user] \"perm\"").queue();
				}
				
			} else {
				//role or user
				String perm = args[4];
			}
		}
	}

	
	
}
