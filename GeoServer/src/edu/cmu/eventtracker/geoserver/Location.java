package edu.cmu.eventtracker.geoserver;

public class Location {
	private float lng;
	private float lat;
	private String username;
	private int event_id = 0; //default to null/0?
	
	public float getLng() {
		return lng;
	}
	public void setLng(float lng) {
		this.lng = lng;
	}
	public float getLat() {
		return lat;
	}
	public void setLat(float lat) {
		this.lat = lat;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public int getEvent() {
		return event_id;
	}
	public void setEvent(int event_id) {
		this.event_id = event_id;
	}
}
