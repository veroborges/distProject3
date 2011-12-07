package edu.cmu.eventtracker.actionhandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import edu.cmu.eventtracker.action.GetEventAction;
import edu.cmu.eventtracker.action.GetUserEvents;
import edu.cmu.eventtracker.dto.Event;

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
							"select location.event_id, location.lat, location.lng from location join (select event_id, username, max(timestamp) as "
									+ "timestamp from location where event_id is not null and username=? group by event_id, username) as q "
									+ " on location.event_id = q.event_id and location.username = "
									+ "q.username and location.timestamp = q.timestamp");

			userEventsStatement.setString(1, action.getUsername());
			userEventsStatement.execute();
			rs = userEventsStatement.getResultSet();

			// populate events list with events returned
			while (rs.next()) {
				lat = rs.getDouble("lat");
				lng = rs.getDouble("lng");

				Event event = geoContext.getService().getLocatorService()
						.getLocationShardServer(lat, lng)
						.execute(new GetEventAction(rs.getString("event_id")));

				if (event != null) {
					events.add(event);
				}
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
