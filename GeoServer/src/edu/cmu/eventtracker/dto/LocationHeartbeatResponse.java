package edu.cmu.eventtracker.dto;

import java.io.Serializable;
import java.util.ArrayList;

public class LocationHeartbeatResponse implements Serializable {

	private ArrayList<Event> events;
	private boolean canCreateEvent;

	/**
	 * @return the events
	 */
	public ArrayList<Event> getEvents() {
		return events;
	}
	/**
	 * @param events
	 *            the events to set
	 */
	public void setEvents(ArrayList<Event> events) {
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
