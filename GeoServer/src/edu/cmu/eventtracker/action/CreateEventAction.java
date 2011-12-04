package edu.cmu.eventtracker.action;

import edu.cmu.eventtracker.dto.Event;
import edu.cmu.eventtracker.dto.Location;

public class CreateEventAction implements Action<Event>, Synchronous {
	private Event event;

	public CreateEventAction() {
	}

	public CreateEventAction(double lat, double lng, String username,
			String eventName) {
		event = new Event();
		event.setLocation(new Location(null, lat, lng, username, null, null));
		event.setName(eventName);
	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

}
