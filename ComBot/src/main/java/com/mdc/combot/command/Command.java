package com.mdc.combot.command;

import com.mdc.combot.ComBot;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * An interface for all commands
 * @author xDestx
 *
 */
public interface Command {

	/**
	 * Get the label for this command
	 * @return The command label
	 */
	String getLabel();
	
	/**
	 * Method for when a user executes a command with this label
	 * @param bot The bot
	 * @param e The event
	 */
	void called(ComBot bot, MessageReceivedEvent e);
}
