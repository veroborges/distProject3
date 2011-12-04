package edu.cmu.eventtracker.action;

public class ReplicationAction implements Action<Void> {

	private Action<?> action;

	public ReplicationAction() {
	}

	public ReplicationAction(Action<?> action) {
		this.action = action;
	}

	public Action<?> getAction() {
		return action;
	}

	public void setAction(Action<?> action) {
		this.action = action;
	}

}
