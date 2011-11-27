package edu.cmu.eventtracker.action;

import java.util.List;

import edu.cmu.eventtracker.dto.Location;

public class GetUserLocations implements Action<List<Location>> {

	private String username;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}
