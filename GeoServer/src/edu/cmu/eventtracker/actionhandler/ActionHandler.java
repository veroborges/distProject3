package edu.cmu.eventtracker.actionhandler;

import edu.cmu.eventtracker.action.Action;

public interface ActionHandler<A extends Action<R>, R> {

	public R performAction(A action, ActionContext context);
}
