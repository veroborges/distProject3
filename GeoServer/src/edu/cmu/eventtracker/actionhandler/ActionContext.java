package edu.cmu.eventtracker.actionhandler;

import edu.cmu.eventtracker.action.Action;

public interface ActionContext {
	public <A extends Action<R>, R> R execute(A action);
}
