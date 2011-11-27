package edu.cmu.eventtracker.action;

import edu.cmu.eventtracker.dto.PingResponse;

public class PingAction implements ReplicatableAction<PingResponse> {

	private double lat;
	private double lng;
	private String username;
	private Long eventId;

	public PingAction() {
	}

	public PingAction(double lat, double lng, String username) {
		this.lat = lat;
		this.lng = lng;
		this.username = username;
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

	public Long getEventId() {
		return eventId;
	}

	public void setEventId(Long eventId) {
		this.eventId = eventId;
	}

}
