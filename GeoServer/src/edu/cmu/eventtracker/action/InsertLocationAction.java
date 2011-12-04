package edu.cmu.eventtracker.action;

import edu.cmu.eventtracker.dto.Location;

public class InsertLocationAction implements ReplicatableAction<Void> {

	private Location location;
	private boolean forUserShard;

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

	public boolean isForUserShard() {
		return forUserShard;
	}

	public void setForUserShard(boolean forUserShard) {
		this.forUserShard = forUserShard;
	}
}
