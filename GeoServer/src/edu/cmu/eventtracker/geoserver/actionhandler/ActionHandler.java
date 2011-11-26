package edu.cmu.eventtracker.geoserver.actionhandler;

import edu.cmu.eventtracker.geoserver.action.Action;

public interface ActionHandler<A extends Action<R>, R> {

	public R performAction(A action, ActionContext context);
}
