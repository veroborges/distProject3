package edu.cmu.eventtracker.action;

import edu.cmu.eventtracker.dto.Location;
import edu.cmu.eventtracker.dto.LocationHeartbeatResponse;

public class LocationHeartbeatAction implements Action<LocationHeartbeatResponse> {

	private Location location;

	public LocationHeartbeatAction() {
	}

	public LocationHeartbeatAction(Location location) {
		this.location = location;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

}
