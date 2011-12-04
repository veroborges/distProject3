package edu.cmu.eventtracker.actionhandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
					.getUsersConnection()
					.prepareStatement(
							"select lat, lng, event_id from location where username= ?");

			userLocationsStatement.setString(1, action.getUsername());
			rs = userLocationsStatement.getResultSet();

			// populate location lists with rows returned
			while (rs.next()) {
				// create location object
				Location loc = new Location();
				loc.setLat(rs.getFloat("lat"));
				loc.setLng(rs.getFloat("lng"));
				loc.setUsername(action.getUsername());
				loc.setEventId(rs.getString("event_id"));
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
