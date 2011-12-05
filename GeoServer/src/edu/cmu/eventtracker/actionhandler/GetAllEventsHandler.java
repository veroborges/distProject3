package edu.cmu.eventtracker.actionhandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import edu.cmu.eventtracker.action.GetAllEventsAction;
import edu.cmu.eventtracker.dto.Event;

public class GetAllEventsHandler implements
		ActionHandler<GetAllEventsAction, List<Event>> {

	@Override
	public List<Event> performAction(GetAllEventsAction action,
			ActionContext context) {
		GeoServiceContext geoContext = (GeoServiceContext) context;
		List<Event> events = new ArrayList<Event>();
		ResultSet rs = null;
		Double lat, lng;

		try {
			// statement to get all of a user's events
			PreparedStatement userEventsStatement = geoContext
					.getUsersConnection().prepareStatement("");

			// userEventsStatement.setString(1, action.getUsername());
			// rs = userEventsStatement.getResultSet();

			// populate events list with events returned

			while (rs.next()) {
				// lat = rs.getDouble(lat);
				// lng = rs.getDouble(lng);
				// Location loc = new Location(null, lat, lng, null, null,
				// null);
				// Event event = new Event(rs.getString("event_id"),
				// rs.getString("eventname"), loc);
				// events.add(event);
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
