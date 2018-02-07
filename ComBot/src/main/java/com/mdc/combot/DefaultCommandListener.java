package com.mdc.combot;

import java.util.LinkedList;
import java.util.Queue;

import com.mdc.combot.command.Command;

import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class DefaultCommandListener extends ListenerAdapter {

	private ComBot b;
	
	public DefaultCommandListener(ComBot b) {
		this.b = b;
	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent e) {
		String msg = e.getMessage().getContentRaw();
		if(msg.contains(b.getCommandPrefix())) {
			String msgWithoutCmdPrefix = msg.substring(b.getCommandPrefix().length());
			if(msgWithoutCmdPrefix.length() > 0) {
				String label;
				if(msgWithoutCmdPrefix.indexOf(' ') != -1) {
					label = msgWithoutCmdPrefix.substring(0, msgWithoutCmdPrefix.indexOf(' '));
				} else {
					label = msgWithoutCmdPrefix;
				}
				
				Queue<Command> commandsCalled = new LinkedList<Command>();
				if(e.getMessage().getContentRaw().startsWith(b.getCommandPrefix())) {
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
	
	/**
	 * Prints out ready when the bot is ready.
	 * @param e The ready event
	 */
	@Override
	public void onReady(ReadyEvent e) {
		System.out.println("Login good! Logged in as " + e.getJDA().getSelfUser().getName());
	}
	
}
