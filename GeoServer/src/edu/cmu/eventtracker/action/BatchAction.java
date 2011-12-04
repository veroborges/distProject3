package edu.cmu.eventtracker.action;

import java.util.List;

public class BatchAction implements Action<Void> {

	private List<? extends Action<?>> actions;

	public BatchAction() {

	}

	public BatchAction(List<? extends Action<?>> actions) {
		this.setActions(actions);
	}

	public List<? extends Action<?>> getActions() {
		return actions;
	}

	public void setActions(List<? extends Action<?>> actions) {
		this.actions = actions;
	}
}
