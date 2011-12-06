package edu.cmu.eventtracker.action;

import edu.cmu.eventtracker.dto.Event;

public class GetEventAction implements Action<Event>, ReadOnlyAction {

	private String eventId;

	public GetEventAction() {
	}

	public GetEventAction(String eventId) {
		this.eventId = eventId;
	}

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

}
