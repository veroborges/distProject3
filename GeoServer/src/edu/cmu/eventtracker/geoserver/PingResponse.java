package edu.cmu.eventtracker.geoserver;

import java.util.ArrayList;

public class PingResponse {

	private ArrayList<String> events;
	private boolean canCreateEvent;
	
	
	/**
	 * @return the events
	 */
	public ArrayList<String> getEvents() {
		return events;
	}
	/**
	 * @param events
	 *            the events to set
	 */
	public void setEvents(ArrayList<String> events) {
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
