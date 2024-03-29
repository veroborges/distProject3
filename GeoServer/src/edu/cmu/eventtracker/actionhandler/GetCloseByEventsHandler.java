package edu.cmu.eventtracker.actionhandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;

import edu.cmu.eventtracker.action.GetCloseByEvents;
import edu.cmu.eventtracker.action.GetEventAction;
import edu.cmu.eventtracker.dto.Event;

public class GetCloseByEventsHandler
		implements
			ActionHandler<GetCloseByEvents, HashMap<String, Event>> {

	@Override
	public HashMap<String, Event> performAction(GetCloseByEvents action,
			ActionContext context) {
		GeoServiceContext geoContext = (GeoServiceContext) context;
		HashMap<String, Event> closeByEvents = new HashMap<String, Event>();

		PreparedStatement s;
		try {
			s = geoContext
					.getLocationsConnection()
					.prepareStatement(
							"select event_id, event.name as eventname, count(*) as count from location join "
									+ "(Select username, max(timestamp) as timestamp from location  where ? <= lat and lat < ? and ? <= lng and lng < ? and timestamp > ? group by username)"
									+ " s on (location.username = s.username and location.timestamp = s.timestamp) left join event on location.event_id = event.id group by event_id, event.name");

			s.setDouble(1, action.getLatmin());
			s.setDouble(2, action.getLatmax());
			s.setDouble(3, action.getLngmin());
			s.setDouble(4, action.getLngmax());
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.MINUTE, -LocationHeartbeatHandler.MAX_PERIOD);
			s.setTimestamp(5, new Timestamp(calendar.getTime().getTime()));
			s.execute();
			ResultSet rs = s.getResultSet();
			while (rs.next()) {
				int count = rs.getInt("count");
				String eventId = rs.getString("event_id");
				Event event;
				if (eventId != null) {
					event = geoContext.execute(new GetEventAction(eventId));
				} else {
					event = new Event();
				}
				if (event != null) {
					event.setParticipantCount(count);
					closeByEvents.put(eventId, event);
				}
			}
			return closeByEvents;
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
	}
}
