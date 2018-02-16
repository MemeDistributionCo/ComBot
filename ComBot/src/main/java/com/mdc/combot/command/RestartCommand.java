package com.mdc.combot.command;

import com.mdc.combot.BotLauncher;
import com.mdc.combot.ComBot;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class RestartCommand implements Command {

	@Override
	public String getLabel() {
		return "restart";
	}

	@Override
	public void called(ComBot bot, MessageReceivedEvent e) {
		/*
		 * For now, just restart on call
		 */
		if(bot.memberHasPerm("bot.restart", e.getMember())) {
			BotLauncher.restart();
		} else {
			System.out.println("User tried to restart bot without permission");
		}
		//Goodbye, cruel world.
	}

}
