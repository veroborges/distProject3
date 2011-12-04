package edu.cmu.eventtracker.actionhandler;

import edu.cmu.eventtracker.action.PingAction;

public class PingHandler implements ActionHandler<PingAction, Void> {

	@Override
	public Void performAction(PingAction action, ActionContext context) {
		return null;
	}

}
