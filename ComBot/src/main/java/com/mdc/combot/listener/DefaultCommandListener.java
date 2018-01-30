package com.mdc.combot.listener;

import java.util.ArrayList;
import java.util.List;

import com.mdc.combot.ComBot;
import com.mdc.combot.command.Command;
import com.mdc.combot.event.CommandSentEvent;
import com.mdc.combot.util.Config;

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
		String msgWithoutCmdPrefix = msg.substring(Config.getComandPrefix().length());
		String label;
		if(msgWithoutCmdPrefix.indexOf(' ') != -1) {
			label = msgWithoutCmdPrefix.substring(0, msgWithoutCmdPrefix.indexOf(' '));
		} else {
			label = msgWithoutCmdPrefix;
		}
		List<CommandSentEvent> cmdEvents = new ArrayList<CommandSentEvent>();
		if(e.getMessage().getContentRaw().startsWith(Config.getComandPrefix())) {
			for(Command c : b.getRegisteredCommands()) {
				if(c.getLabel().equalsIgnoreCase(label)) {
					CommandSentEvent cse = new CommandSentEvent(e, b, c);
					cmdEvents.add(cse);
				}
			}
		}
		for(CommandSentEvent cmd : cmdEvents) {
			b.invokeEvent(cmd);
		}
	}
	
}
