package edu.cmu.eventtracker.action;

import java.util.List;

public class GetUserEvents implements Action<List<String>> {

	private String username;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}
