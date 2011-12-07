package edu.cmu.eventtracker.actionhandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.cmu.eventtracker.action.GetAllEventsAction;
import edu.cmu.eventtracker.dto.Event;
import edu.cmu.eventtracker.dto.Location;

public class GetAllEventsHandler implements
		ActionHandler<GetAllEventsAction, List<Event>> {

	@Override
	public List<Event> performAction(GetAllEventsAction action,
			ActionContext context) {
		GeoServiceContext geoContext = (GeoServiceContext) context;
		try {
			PreparedStatement s = geoContext
					.getLocationsConnection()
					.prepareStatement(
							"select event.id as event_id, name, location.id as location_id, lat, lng, username, location.timestamp as location_timestamp"
									+ " from location join (select event_id, min(timestamp) as timestamp from location where ? <= lat and lat <= ? and ? <= lng and lng <= ? group by event_id) q on location.event_id = q.event_id and location.timestamp = q.timestamp join event on event.id = location.event_id");

			s.setDouble(1, action.getLat1());
			s.setDouble(2, action.getLat2());
			s.setDouble(3, action.getLng1());
			s.setDouble(4, action.getLng2());
			s.execute();
			ResultSet rs = s.getResultSet();
			List<Event> events = new ArrayList<Event>();

			while (rs.next()) {
				Event event = new Event(rs.getString("event_id"),
						rs.getString("name"), new Location(
								rs.getString("location_id"),
								rs.getDouble("lat"), rs.getDouble("lng"), null,
								rs.getString("event_id"), new Date(rs
										.getTimestamp("location_timestamp")
										.getTime())));
				events.add(event);
			}

			return events;
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}

	}
}
