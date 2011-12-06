package edu.cmu.eventtracker.action;

import java.util.List;

import edu.cmu.eventtracker.dto.Event;

public class GetUserEvents implements Action<List<Event>>, ReadOnlyAction {
	private String username;

	public GetUserEvents(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}
