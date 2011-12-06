package edu.cmu.eventtracker.actionhandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import edu.cmu.eventtracker.action.GetUserEvents;
import edu.cmu.eventtracker.dto.Event;
import edu.cmu.eventtracker.dto.Location;

public class GetUserEventsHandler implements
		ActionHandler<GetUserEvents, List<Event>> {

	@Override
	public List<Event> performAction(GetUserEvents action, ActionContext context) {
		GeoServiceContext geoContext = (GeoServiceContext) context;
		List<Event> events = new ArrayList<Event>();
		ResultSet rs = null;
		Double lat, lng;

		try {

			// statement to get all of a user's events
			PreparedStatement userEventsStatement = geoContext
					.getUsersConnection()
					.prepareStatement(
							"select event_id from (select event_id, username, max(timestamp) as "
									+ "timestamp from location where event_id is not null and username=? group by event_id, username) "
									+ " join location on location.event_id = q.event_id and location.username = "
									+ "q.username and location.timestamp = q.timestamp");

			userEventsStatement.setString(1, action.getUsername());
			rs = userEventsStatement.getResultSet();

			// populate events list with events returned
			while (rs.next()) {
				geoContext
						.getService()
						.getLocatorService()
						.getLocationShard(rs.getDouble("lat"),
								rs.getDouble("lng"));
			}

			System.out.println(rs.next());
			lat = rs.getDouble("location.lat");
			lng = rs.getDouble("location.lng");
			Location loc = new Location(null, lat, lng, null, null, null);
			Event event = new Event(rs.getString("event_id"),
					rs.getString("eventname"), loc);
			events.add(event);
			return events;

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
