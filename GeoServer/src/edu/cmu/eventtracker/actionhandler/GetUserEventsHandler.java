package edu.cmu.eventtracker.actionhandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import edu.cmu.eventtracker.action.GetUserEvents;

public class GetUserEventsHandler
		implements
			ActionHandler<GetUserEvents, List<String>> {

	@Override
	public List<String> performAction(GetUserEvents action,
			ActionContext context) {
		GeoServiceContext geoContext = (GeoServiceContext) context;
		List<String> events = new ArrayList<String>();
		ResultSet rs = null;

		try {
			// statement to get all of a user's events
			PreparedStatement userEventsStatement = geoContext.getUsersConnection()
					.prepareStatement("select event.name from event join userevent on event.id = userevent.eventid join users on userevent.username = users.username where userevent.username=?");

			userEventsStatement.setString(1, action.getUsername());
			rs = userEventsStatement.getResultSet();

			// popule events list with eventnames returned
			while (rs.next()) {
				events.add(rs.getString("name"));
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
