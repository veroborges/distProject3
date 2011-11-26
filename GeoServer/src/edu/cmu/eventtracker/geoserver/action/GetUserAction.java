package edu.cmu.eventtracker.geoserver.action;

import edu.cmu.eventtracker.geoserver.dto.User;

public class GetUserAction implements Action<User> {
	private String username;

	public GetUserAction() {

	}

	public GetUserAction(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}
