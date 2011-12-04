package edu.cmu.eventtracker.actionhandler;

import edu.cmu.eventtracker.action.Action;
import edu.cmu.eventtracker.action.BatchAction;

public class BatchHandler implements ActionHandler<BatchAction, Void> {

	@Override
	public Void performAction(BatchAction batchAction, ActionContext context) {
		for (Action<?> action : batchAction.getActions()) {
			context.execute(action);
		}
		return null;
	}

}
