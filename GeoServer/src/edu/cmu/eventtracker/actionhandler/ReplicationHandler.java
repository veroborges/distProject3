package edu.cmu.eventtracker.actionhandler;

import edu.cmu.eventtracker.action.ReplicationAction;

public class ReplicationHandler
		implements
			ActionHandler<ReplicationAction, Void> {

	@Override
	public Void performAction(ReplicationAction action, ActionContext context) {
		context.execute(action.getAction());
		return null;
	}

}
