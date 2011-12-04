package edu.cmu.eventtracker.action;

public class UserShardAction {

	private Action<?> action;
	private String username;

	public UserShardAction(Action<?> action, String username) {
		this.action = action;
		this.username = username;
	}

	public Action<?> getAction() {
		return action;
	}
	public void setAction(Action<?> action) {
		this.action = action;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}

}
