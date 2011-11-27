package edu.cmu.eventtracker.actionhandler;

import java.sql.SQLException;
import java.sql.Statement;

import edu.cmu.eventtracker.action.ClearUsersDBAction;

public class ClearUsersDBHandler
		implements
			ActionHandler<ClearUsersDBAction, Void> {

	@Override
	public Void performAction(ClearUsersDBAction action, ActionContext context) {
		GeoServiceContext geoContext = (GeoServiceContext) context;

		try {
			Statement ps = geoContext.getUsersConnection().createStatement();
			ps.addBatch("Delete from users");
			ps.addBatch("Delete from userevent");
			ps.addBatch("Delete from event");
			ps.addBatch("Delete from location");
			ps.executeBatch();
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
		return null;
	}

}
