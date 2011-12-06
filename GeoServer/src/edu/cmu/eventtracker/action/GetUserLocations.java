package edu.cmu.eventtracker.action;

import java.util.List;

import edu.cmu.eventtracker.dto.Location;

public class GetUserLocations implements Action<List<Location>>, ReadOnlyAction {

	private String username;

	public GetUserLocations() {
	}

	public GetUserLocations(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}
