package com.mdc.combot;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import com.mdc.combot.command.Command;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class DefaultCommandListener extends ListenerAdapter {

	private ComBot b;
	
	private Map<String,String> prefixMap;
	private String prefix;
	private boolean singlePrefix;
	
	public DefaultCommandListener(ComBot b) {
		this.b = b;
		updatePrefix();
	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent e) {
		String msg = e.getMessage().getContentRaw();
		String currentPrefix;
		if(singlePrefix) {
			currentPrefix = this.prefix;
		} else {
			currentPrefix = this.prefixMap.get(e.getGuild().getId());
			if(currentPrefix == null) {
				currentPrefix = this.prefix;
			}
		}
		if(msg.contains(currentPrefix)) {
			String msgWithoutCmdPrefix = msg.substring(currentPrefix.length());
			if(msgWithoutCmdPrefix.length() > 0) {
				String label;
				if(msgWithoutCmdPrefix.indexOf(' ') != -1) {
					label = msgWithoutCmdPrefix.substring(0, msgWithoutCmdPrefix.indexOf(' '));
				} else {
					label = msgWithoutCmdPrefix;
				}
				
				Queue<Command> commandsCalled = new LinkedList<Command>();
				if(e.getMessage().getContentRaw().startsWith(currentPrefix)) {
					for(Command c : b.getRegisteredCommands()) {
						if(c.getLabel().equalsIgnoreCase(label)) {
							commandsCalled.add(c);
						}
					}
				}
				for(Command cmd : commandsCalled) {
					cmd.called(b, e);
				}
			}
		}
		
	}
	
	
	public void updatePrefix() {
		prefix = b.getCommandPrefix((Guild)null);

		prefixMap = new HashMap<String,String>();
		if(b.getGuildSpecificConfig() != null) {
			for(String c : b.getGuildSpecificConfig().keySet()) {
				prefixMap.put(c, b.getGuildSpecificConfig().get(c).get("command-prefix"));
			}
		}
		
		if(!b.isMultiServerOn()) {
			this.singlePrefix = true;
		} else {
			this.singlePrefix = false;
		}
	}
	
	/**
	 * Prints out ready when the bot is ready.
	 * @param e The ready event
	 */
	@Override
	public void onReady(ReadyEvent e) {
		System.out.println("Login good! Logged in as " + e.getJDA().getSelfUser().getName());
	}
	
}
