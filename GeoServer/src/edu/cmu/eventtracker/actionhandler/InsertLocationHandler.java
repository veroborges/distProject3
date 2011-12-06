package edu.cmu.eventtracker.actionhandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.cmu.eventtracker.action.InsertLocationAction;
import edu.cmu.eventtracker.action.UserShardAction;
import edu.cmu.eventtracker.dto.Location;

public class InsertLocationHandler
		implements
			ActionHandler<InsertLocationAction, Void> {

	@Override
	public Void performAction(InsertLocationAction action, ActionContext context) {
		GeoServiceContext geoContext = (GeoServiceContext) context;
		try {
			Connection connection;
			if (action.isForUserShard()) {
				connection = geoContext.getUsersConnection();
			} else {
				connection = geoContext.getLocationsConnection();
			}
			PreparedStatement statement = connection
					.prepareStatement("Insert into location(id, lat, lng, username, timestamp, event_id) values (?, ?, ?, ?, ?, ?)");

			Location location = action.getLocation();
			statement.setString(1, location.getId());
			statement.setDouble(2, location.getLat());
			statement.setDouble(3, location.getLng());
			statement.setString(4, location.getUsername());
			statement.setTimestamp(5, new Timestamp(location.getTimestamp()
					.getTime()));
			if (location.getEventId() == null) {
				statement.setNull(6, java.sql.Types.CHAR);
			} else {
				statement.setString(6, location.getEventId());
			}
			Logger.getLogger("GeoServer").log(
					Level.INFO,
					"Inserting location to "
							+ geoContext.getService().getUrl()
							+ " as "
							+ (geoContext.getService().isMaster()
									? "master"
									: "slave")
							+ " to "
							+ (action.isForUserShard()
									? "user db"
									: "location db") + " " + location.getId());
			statement.execute();

			if (!action.isForUserShard() && geoContext.getService().isMaster()) {
				InsertLocationAction locationAction = new InsertLocationAction(
						action.getLocation());
				locationAction.setForUserShard(true);
				geoContext.addUserShardAction(new UserShardAction(
						locationAction, action.getLocation().getUsername()));
			}
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
		return null;
	}
}
