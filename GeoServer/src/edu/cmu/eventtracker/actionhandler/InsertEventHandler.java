package edu.cmu.eventtracker.actionhandler;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.cmu.eventtracker.action.InsertEventAction;

public class InsertEventHandler
		implements
			ActionHandler<InsertEventAction, Void> {

	public static final double RADIUS = 0.5; // km
	public static final int MIN_COUNT = 10;
	public static final int MAX_PERIOD = 60; // minutes

	@Override
	public Void performAction(InsertEventAction action, ActionContext context) {
		GeoServiceContext geoContext = (GeoServiceContext) context;
		try {
			PreparedStatement statement = geoContext.getLocationsConnection()
					.prepareStatement(
							"Insert into event(id, name) values(?, ?)");

			statement.setString(1, action.getEvent().getId());
			statement.setString(2, action.getEvent().getName());
			statement.execute();
			Logger.getLogger("GeoServer").log(Level.INFO,
					"Inserting an event " + action.getEvent().getId());
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
		return null;
	}
}
