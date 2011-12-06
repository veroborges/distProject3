package edu.cmu.eventtracker.actionhandler;

import edu.cmu.eventtracker.action.DisableSlaveFailover;

public class DisableSlaveFailoverHandler
		implements
			ActionHandler<DisableSlaveFailover, Void> {

	@Override
	public Void performAction(DisableSlaveFailover action, ActionContext context) {
		GeoServiceContext geoContext = (GeoServiceContext) context;
		geoContext.getService().disableSlaveFailover();
		return null;
	}

}
