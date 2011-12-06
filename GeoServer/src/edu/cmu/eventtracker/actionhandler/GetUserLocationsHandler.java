package edu.cmu.eventtracker.actionhandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.cmu.eventtracker.action.GetUserLocations;
import edu.cmu.eventtracker.dto.Location;

public class GetUserLocationsHandler
		implements
			ActionHandler<GetUserLocations, List<Location>> {

	@Override
	public List<Location> performAction(GetUserLocations action,
			ActionContext context) {
		GeoServiceContext geoContext = (GeoServiceContext) context;
		List<Location> locations = new ArrayList<Location>();
		ResultSet rs = null;
		try {
			// statement to get all of a user's locations
			PreparedStatement userLocationsStatement = geoContext
					.getUsersConnection().prepareStatement(
							"select * from location where username= ?");

			userLocationsStatement.setString(1, action.getUsername());
			userLocationsStatement.execute();
			rs = userLocationsStatement.getResultSet();

			// populate location lists with rows returned
			while (rs.next()) {
				// create location object
				Location loc = new Location(rs.getString("id"),
						rs.getDouble("lat"), rs.getDouble("lng"),
						rs.getString("username"), rs.getString("event_id"),
						new Date(rs.getTimestamp("timestamp").getTime()));
				locations.add(loc);
			}

			return locations;

		} catch (SQLException e) {
			throw new IllegalStateException(e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					throw new IllegalStateException(e);
				}
			}
		}
	}
}
