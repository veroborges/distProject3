package edu.cmu.eventtracker.dto;

import java.io.Serializable;
import java.util.HashMap;

public class PingResponse implements Serializable {

	private HashMap<Long, Event> events;
	private boolean canCreateEvent;

	/**
	 * @return the events
	 */
	public HashMap<Long, Event> getEvents() {
		return events;
	}
	/**
	 * @param events
	 *            the events to set
	 */
	public void setEvents(HashMap<Long, Event> events) {
		this.events = events;
	}
	/**
	 * @return the createEvent
	 */
	public boolean canCreateEvent() {
		return canCreateEvent;
	}
	/**
	 * @param canCreateEvent
	 *            the canCreateEvent to set
	 */
	public void setCanCreateEvent(boolean canCreateEvent) {
		this.canCreateEvent = canCreateEvent;
	}

}
