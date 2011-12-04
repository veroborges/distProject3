package edu.cmu.eventtracker.action;

import edu.cmu.eventtracker.dto.Event;

public class InsertEventAction implements ReplicatableAction<Void> {

	private Event event;

	public InsertEventAction() {

	}

	public InsertEventAction(Event Event) {
		this.setEvent(Event);
	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

}
