package edu.cmu.eventtracker.actionhandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import edu.cmu.eventtracker.action.GetEventAction;
import edu.cmu.eventtracker.dto.Event;
import edu.cmu.eventtracker.dto.Location;

public class GetEventHandler implements ActionHandler<GetEventAction, Event> {

	@Override
	public Event performAction(GetEventAction action, ActionContext context) {
		GeoServiceContext geoContext = (GeoServiceContext) context;
		try {
			PreparedStatement s = geoContext
					.getLocationsConnection()
					.prepareStatement(
							"select event.id as event_id, name, location.id as location_id, lat, lng, username, location.timestamp as location_timestamp"
									+ " from location join (select event_id, min(timestamp) as timestamp from location where event_id=? group by event_id) q on location.event_id = q.event_id and location.timestamp = q.timestamp join event on event.id = location.event_id");

			s.setString(1, action.getEventId());

			s.execute();
			ResultSet rs = s.getResultSet();
			while (rs.next()) {
				Event event = new Event(rs.getString("event_id"),
						rs.getString("name"), new Location(
								rs.getString("location_id"),
								rs.getDouble("lat"), rs.getDouble("lng"),
								rs.getString("username"),
								rs.getString("event_id"), new Date(rs
										.getTimestamp("location_timestamp")
										.getTime())));
				return event;
			}
			return null;
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
	}

}
