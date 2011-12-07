package edu.cmu.eventtracker.action;

import java.util.HashMap;

import edu.cmu.eventtracker.dto.Event;

public class GetCloseByEvents
		implements
			Action<HashMap<String, Event>>,
			ReadOnlyAction {

	private double lat;
	private double lng;

	public GetCloseByEvents() {
	}

	public GetCloseByEvents(double lat, double lng) {
		this.lat = lat;
		this.lng = lng;
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
}
