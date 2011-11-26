package edu.cmu.eventtracker.geoserver.dto;

import java.io.Serializable;
import java.util.HashMap;

public class PingResponse implements Serializable {

	private HashMap<Event, Integer> events;
	private boolean canCreateEvent;

	/**
	 * @return the events
	 */
	public HashMap<Event, Integer> getEvents() {
		return events;
	}
	/**
	 * @param events
	 *            the events to set
	 */
	public void setEvents(HashMap<Event, Integer> events) {
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
