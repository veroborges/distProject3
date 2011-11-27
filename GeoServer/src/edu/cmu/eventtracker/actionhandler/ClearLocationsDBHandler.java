package edu.cmu.eventtracker.actionhandler;

import java.sql.SQLException;
import java.sql.Statement;

import edu.cmu.eventtracker.action.ClearLocationsDBAction;

public class ClearLocationsDBHandler
		implements
			ActionHandler<ClearLocationsDBAction, Void> {

	@Override
	public Void performAction(ClearLocationsDBAction action,
			ActionContext context) {
		GeoServiceContext geoContext = (GeoServiceContext) context;

		try {
			Statement ps = geoContext.getLocationsConnection()
					.createStatement();
			ps.addBatch("Delete from event");
			ps.addBatch("Delete from location");
			ps.executeBatch();
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
		return null;
	}

}
