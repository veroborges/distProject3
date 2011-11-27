package edu.cmu.eventtracker.geoserver.dto;

import java.io.Serializable;

public class Location implements Serializable {
	private double id;
	private double lat;
	private double lng;
	private String username;
	private long eventId = 0; // default to null/0?

	public Location() {

	}

	public Location(long id, double lat, double lng, String username,
			long eventId) {
		this.setId(id);
		this.lng = lng;
		this.lat = lat;
		this.username = username;
		this.eventId = eventId;
	}

	public double getLng() {
		return lng;
	}
	public void setLng(double lng) {
		this.lng = lng;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}

	public long getEventId() {
		return eventId;
	}

	public void setEventId(long eventId) {
		this.eventId = eventId;
	}

	public double getId() {
		return id;
	}

	public void setId(double id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(id);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Location other = (Location) obj;
		if (Double.doubleToLongBits(id) != Double.doubleToLongBits(other.id))
			return false;
		return true;
	}

}
