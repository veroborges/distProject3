package edu.cmu.eventtracker.geoserver.action;

import edu.cmu.eventtracker.geoserver.dto.Event;

public class CreateEventAction implements Action<Event> {
	private double lat;
	private double lng;
	private String username;
	private String eventName;

	public CreateEventAction() {
	}

	public CreateEventAction(double lat, double lng, String username,
			String eventName) {
		this.lat = lat;
		this.lng = lng;
		this.username = username;
		this.eventName = eventName;
	}

	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLng() {
		return lng;
	}
	public void setLng(double lng) {
		this.lng = lng;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getEventName() {
		return eventName;
	}
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

}
