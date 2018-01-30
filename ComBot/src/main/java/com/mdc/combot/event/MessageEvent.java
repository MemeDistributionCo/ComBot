package com.mdc.combot.event;

import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public abstract class MessageEvent extends Event {

	protected final MessageReceivedEvent e;
	
	public MessageEvent(MessageReceivedEvent e) {
		this.e = e;
	}
	
	public User getSender() {
		return this.e.getAuthor();
	}
	
	public MessageReceivedEvent getInternalEvent() {
		return this.e;
	}
	
}
