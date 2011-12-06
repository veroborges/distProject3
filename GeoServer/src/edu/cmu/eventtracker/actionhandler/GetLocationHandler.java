package edu.cmu.eventtracker.actionhandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import edu.cmu.eventtracker.action.GetLocationAction;
import edu.cmu.eventtracker.dto.Location;

public class GetLocationHandler
		implements
			ActionHandler<GetLocationAction, Location> {

	@Override
	public Location performAction(GetLocationAction action,
			ActionContext context) {
		GeoServiceContext geoContext = (GeoServiceContext) context;
		try {
			PreparedStatement statement = geoContext.getLocationsConnection()
					.prepareStatement("Select * from location where id=?");
			statement.setString(1, action.getLocationId());
			statement.execute();
			ResultSet rs = statement.getResultSet();
			if (rs.next()) {
				return new Location(rs.getString("id"), rs.getDouble("lat"),
						rs.getDouble("lng"), rs.getString("username"),
						rs.getString("event_id"), new Date(rs.getTimestamp(
								"timestamp").getTime()));
			}
			return null;
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
	}

}
