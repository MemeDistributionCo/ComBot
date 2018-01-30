package com.mdc.combot.event;

import com.mdc.combot.ComBot;
import com.mdc.combot.command.Command;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CommandSentEvent extends MessageEvent {

	private final Command c;
	private final ComBot b;
	
	public CommandSentEvent(MessageReceivedEvent e, ComBot b, Command c) {
		super(e);
		this.c = c;
		this.b = b;
	}
	
	public Command getCommand() {
		return this.c;
	}

	@Override
	protected void run() {
		c.called(b, e);
	}

}
