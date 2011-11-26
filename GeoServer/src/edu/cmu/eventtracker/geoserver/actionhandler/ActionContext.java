package edu.cmu.eventtracker.geoserver.actionhandler;

import edu.cmu.eventtracker.geoserver.action.Action;

public interface ActionContext {
	public <A extends Action<R>, R> R execute(A action);
}
