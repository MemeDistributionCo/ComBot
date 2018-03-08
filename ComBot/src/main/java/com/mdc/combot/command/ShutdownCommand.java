package com.mdc.combot.command;

import com.mdc.combot.ComBot;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class ShutdownCommand implements Command {

	@Override
	public String getLabel() {
		return "shutdown";
	}

	@Override
	public void called(ComBot bot, MessageReceivedEvent e) {
		if(bot.memberHasPerm("bot.shutdown", e.getMember())) {
			bot.shutdown();
		}
	}

}
