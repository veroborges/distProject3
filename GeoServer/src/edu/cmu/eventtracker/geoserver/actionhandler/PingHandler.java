package edu.cmu.eventtracker.geoserver.actionhandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;

import com.effectiveJava.GeoLocationService;
import com.effectiveJava.Point;

import edu.cmu.eventtracker.geoserver.action.PingAction;
import edu.cmu.eventtracker.geoserver.dto.Event;
import edu.cmu.eventtracker.geoserver.dto.Location;
import edu.cmu.eventtracker.geoserver.dto.PingResponse;

public class PingHandler implements ActionHandler<PingAction, PingResponse> {

	public static final double RADIUS = 0.5; // km
	public static final int MIN_COUNT = 10;
	public static final int MAX_PERIOD = 60; // minutes

	@Override
	public PingResponse performAction(PingAction action, ActionContext context) {
		GeoServiceContext geoContext = (GeoServiceContext) context;
		PingResponse response = new PingResponse();
		try {
			insertLocation(action.getUsername(), action.getLat(),
					action.getLng(), action.getEventId(), geoContext);
			HashMap<Long, Event> closeByEvents = closeByEvents(action.getLat(),
					action.getLng(), geoContext);
			if (action.getEventId() != null) {
				Event event = closeByEvents.get(action.getEventId());
				if (event == null) {
					throw new NullPointerException(
							"Event with the given id was not found");
				}
				Point[] extremes = GeoLocationService.getExtremePointsFrom(
						new Point(event.getLocation().getLat(), event
								.getLocation().getLng()), RADIUS);
				if (!(extremes[0].getLatitude() <= action.getLat()
						&& extremes[0].getLongitude() <= action.getLng()
						&& action.getLat() <= extremes[1].getLatitude() && action
						.getLng() <= extremes[1].getLongitude())) {
					throw new IllegalStateException(
							"You are too far away from the original event");
				}
			}
			response.setEvents(closeByEvents);
			response.setCanCreateEvent(canCreateNewEvents(closeByEvents));
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
		return response;
	}

	public static long insertLocation(String username, double lat, double lng,
			Long eventId, GeoServiceContext geoContext) throws SQLException {
		PreparedStatement statement = geoContext
				.getLocationsConnection()
				.prepareStatement(
						"Insert into location(lat, lng, username, timestamp, event_id) values (?, ?, ?, ?, ?)",
						Statement.RETURN_GENERATED_KEYS);
		statement.setDouble(1, lat);
		statement.setDouble(2, lng);
		statement.setString(3, username);
		statement.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
		if (eventId == null) {
			statement.setNull(5, java.sql.Types.BIGINT);
		} else {
			statement.setLong(5, eventId);
		}
		statement.execute();
		ResultSet res = statement.getGeneratedKeys();
		long locationId = 0;
		if (res.next()) {
			locationId = res.getLong(1);
		}
		return locationId;
	}
	public static HashMap<Long, Event> closeByEvents(double lat, double lng,
			GeoServiceContext geoContext) throws SQLException {
		HashMap<Long, Event> closeByEvents = new HashMap<Long, Event>();
		Point[] extremePointsFrom = GeoLocationService.getExtremePointsFrom(
				new Point(lat, lng), RADIUS);
		PreparedStatement s = geoContext
				.getLocationsConnection()
				.prepareStatement(
						"select event_id, event.name as eventname, count(*) as count from location join "
								+ "(Select id, username, max(timestamp) from location  where ? <= lat and lat < ? and ? <= lng and lng < ? and timestamp > ? group by id, username)"
								+ " s on location.id = s.id left join event on location.event_id = event.id group by event_id, event.name");
		s.setDouble(1, extremePointsFrom[0].getLatitude());
		s.setDouble(2, extremePointsFrom[0].getLongitude());
		s.setDouble(3, extremePointsFrom[1].getLatitude());
		s.setDouble(4, extremePointsFrom[1].getLongitude());
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, -MAX_PERIOD);
		s.setTimestamp(5, new Timestamp(calendar.getTimeInMillis()));

		s.execute();
		ResultSet rs = s.getResultSet();
		while (rs.next()) {
			int count = rs.getInt("count");
			long eventId = rs.getLong("event_id");
			closeByEvents.put(eventId, getEvent(eventId, geoContext));
			Event event;
			if (eventId != 0) {
				event = getEvent(eventId, geoContext);
			} else {
				event = new Event();
			}
			event.setParticipantCount(count);
			closeByEvents.put(eventId, event);
		}
		return closeByEvents;
	}

	public static boolean canCreateNewEvents(HashMap<Long, Event> closeByEvents) {
		Event usersWithoutEvent = closeByEvents.get(0L);
		if (usersWithoutEvent != null
				&& usersWithoutEvent.getParticipantCount() >= MIN_COUNT) {
			return true;
		} else {
			return false;
		}
	}

	public static Event getEvent(long eventId, GeoServiceContext geoContext)
			throws SQLException {
		PreparedStatement s = geoContext
				.getLocationsConnection()
				.prepareStatement(
						"select event.id, name, lat, lng, username, location.id as location_id from event join location on event.location_id = location.id where event.id=?");
		s.setLong(1, eventId);
		s.execute();
		ResultSet rs = s.getResultSet();
		while (rs.next()) {
			Event event = new Event(rs.getLong("id"), rs.getString("name"),
					new Location(rs.getLong("location_id"),
							rs.getDouble("lat"), rs.getDouble("lng"),
							rs.getString("username"), rs.getLong("id")));
			return event;
		}
		return null;
	}

}
