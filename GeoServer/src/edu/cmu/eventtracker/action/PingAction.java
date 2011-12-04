package edu.cmu.eventtracker.action;

import edu.cmu.eventtracker.dto.Location;
import edu.cmu.eventtracker.dto.PingResponse;

public class PingAction implements Action<PingResponse> {

	private Location location;

	public PingAction() {
	}

	public PingAction(Location location) {
		this.location = location;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

}
