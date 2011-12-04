package edu.cmu.eventtracker.action;

import edu.cmu.eventtracker.dto.Location;

public class InsertLocationAction implements ReplicatableAction<Void> {

	private Location location;

	public InsertLocationAction() {

	}

	public InsertLocationAction(Location location) {
		this.location = location;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}
}
