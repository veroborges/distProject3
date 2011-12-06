package edu.cmu.eventtracker.actionhandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import edu.cmu.eventtracker.action.GetUserEvents;
import edu.cmu.eventtracker.dto.Event;
import edu.cmu.eventtracker.dto.Location;

public class GetUserEventsHandler
		implements
			ActionHandler<GetUserEvents, List<Event>> {

	@Override
	public List<Event> performAction(GetUserEvents action, ActionContext context) {
		GeoServiceContext geoContext = (GeoServiceContext) context;
		List<Event> events = new ArrayList<Event>();
		ResultSet rs = null;
		double lat, lng;

		try {
			// statement to get all of a user's events
			PreparedStatement userEventsStatement = geoContext
					.getUsersConnection()
					.prepareStatement(
							"Select event_id, event.name as eventnamefrom event where exists" // timestamp?
									+ "(Select eventId, max(timestamp) from location WHERE location.username = ? and eventId is not null)");

			userEventsStatement.setString(1, action.getUsername());
			rs = userEventsStatement.getResultSet();

			// populate events list with events returned
			while (rs.next()) {
				lat = rs.getDouble("lat");
				lng = rs.getDouble("lng");
				Location loc = new Location(null, lat, lng, null, null, null);
				Event event = new Event(rs.getString("event_id"),
						rs.getString("eventname"), loc);
				events.add(event);
			}

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
