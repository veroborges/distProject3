package edu.cmu.eventtracker.action;

public class ReplicationAction implements Action<Void> {

	private Action<?> action;
	private String sourceUrl;

	public ReplicationAction() {
	}

	public ReplicationAction(Action<?> action, String sourceUrl) {
		this.action = action;
		this.sourceUrl = sourceUrl;
	}

	public Action<?> getAction() {
		return action;
	}

	public void setAction(Action<?> action) {
		this.action = action;
	}

	public String getSourceUrl() {
		return sourceUrl;
	}

	public void setSourceUrl(String sourceUrl) {
		this.sourceUrl = sourceUrl;
	}

}
