package edu.cmu.eventtracker.action;

import edu.cmu.eventtracker.dto.Location;

public class GetLocationAction implements ReadOnlyAction, Action<Location> {

	private String locationId;

	public GetLocationAction() {
	}

	public GetLocationAction(String locationId) {
		this.locationId = locationId;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

}
