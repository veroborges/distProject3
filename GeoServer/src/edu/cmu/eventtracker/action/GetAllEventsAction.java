package edu.cmu.eventtracker.action;

import java.util.List;

import edu.cmu.eventtracker.dto.Event;
import edu.cmu.eventtracker.dto.Location;

public class GetAllEventsAction implements Action<List<Event>>, ReadOnlyAction {
	private Location location;

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}
}
