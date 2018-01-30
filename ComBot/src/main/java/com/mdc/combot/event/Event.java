package com.mdc.combot.event;

import java.util.Calendar;

import java.util.Date;

/**
 * The base class for any event to happen with this bot.
 * @author xDestx
 *
 */
public abstract class Event {

	private final Date timeOccurred;
	private boolean cancelled;
	private boolean completed;
	
	/**
	 * Create a new Event with a creation time
	 */
	public Event() {
		timeOccurred = Calendar.getInstance().getTime();
		this.cancelled = false;
		this.completed = false;
	}
	
	/**
	 * Get the time that this event was initialized
	 * @return The {@link java.util.Date Date} of when this event was initialized
	 */
	public final Date getCreationTime() {
		return timeOccurred;
	}
	
	/**
	 * Check to tell if this event has been cancelled
	 * @return True, if it has been cancelled.
	 */
	public boolean isCancelled() {
		return this.cancelled;
	}
	
	/**
	 * Set this event's cancel status.
	 * @param cancel To cancel
	 */
	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
	}
	
	public final void enactEvent() {
		if(!this.isCancelled() && !this.completed) {
			run();
			this.completed = true;
		}
	}
	
	/**
	 * Tell whether this event has already occurred.
	 * @return true, if this event has been enacted
	 */
	public boolean getCompleted() {
		return this.completed;
	}
	
	protected abstract void run();
}
