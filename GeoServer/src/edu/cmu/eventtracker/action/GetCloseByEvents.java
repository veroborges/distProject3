package edu.cmu.eventtracker.action;

import java.util.HashMap;

import edu.cmu.eventtracker.dto.Event;

public class GetCloseByEvents
		implements
			Action<HashMap<String, Event>>,
			ReadOnlyAction {

	private double latmin;
	private double lngmin;
	private double latmax;
	private double lngmax;

	public GetCloseByEvents() {
	}

	public GetCloseByEvents(double latmin, double lngmin, double latmax,
			double lngmax) {
		this.latmin = latmin;
		this.lngmin = lngmin;
		this.latmax = latmax;
		this.lngmax = lngmax;
	}

	public double getLatmin() {
		return latmin;
	}

	public void setLatmin(double latmin) {
		this.latmin = latmin;
	}

	public double getLngmin() {
		return lngmin;
	}

	public void setLngmin(double lngmin) {
		this.lngmin = lngmin;
	}

	public double getLatmax() {
		return latmax;
	}

	public void setLatmax(double latmax) {
		this.latmax = latmax;
	}

	public double getLngmax() {
		return lngmax;
	}

	public void setLngmax(double lngmax) {
		this.lngmax = lngmax;
	}

}
