package edu.cmu.eventtracker.geoserver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.caucho.hessian.server.HessianServlet;
import com.effectiveJava.GeoLocationService;
import com.effectiveJava.Point;

public class GeoServiceImpl extends HessianServlet implements GeoService {
	public final String driver = "org.apache.derby.jdbc.EmbeddedDriver";
	public final String protocol = "jdbc:derby:";

	private final Connection usersConnection;
	private final Connection locationsConnection;
	private final double RADIUS = 0.5; // km
	private final int MIN_COUNT = 10;
	private final int MAX_PERIOD = 60; // minutes
	public GeoServiceImpl() {
		try {
			usersConnection = DriverManager.getConnection(protocol
					+ "usersDB;create=true", null);
			locationsConnection = DriverManager.getConnection(protocol
					+ "locationsDB;create=true", null);
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public List<Location> getUserLocations(String username) {
		// select statement here
		return null;
	}

	public PingResponse ping(double lat, double lng, String username) {
		PingResponse response = new PingResponse();
		response.setEvents(new ArrayList<String>());
		try {
			PreparedStatement statement = locationsConnection
					.prepareStatement("Insert into locations(lat, lng, username, timestamp) values (?, ?, ?, ?)");
			statement.setDouble(1, lat);
			statement.setDouble(2, lng);
			statement.setString(3, username);
			statement
					.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
			statement.execute();
			Point[] extremePointsFrom = GeoLocationService
					.getExtremePointsFrom(new Point(lat, lng), RADIUS);
			PreparedStatement s = locationsConnection
					.prepareStatement("select event_id, event.name as eventname, count(*) as count from location join (Select id, username, max(timestamp) from location  where ? <= lat and lat < ? and ? <= lng and lng < ? and timestamp > ? and username != ? group by id, username) s on location.id = s.id left join event on location.event_id = event.id group by event_id, event.name");
			s.setDouble(1, extremePointsFrom[0].getLatitude());
			s.setDouble(2, extremePointsFrom[0].getLongitude());
			s.setDouble(3, extremePointsFrom[1].getLatitude());
			s.setDouble(4, extremePointsFrom[1].getLongitude());
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.MINUTE, -MAX_PERIOD);
			s.setTimestamp(5, new Timestamp(calendar.getTimeInMillis()));
			s.setString(6, username);

			s.execute();
			ResultSet rs = s.getResultSet();
			while (rs.next()) {
				int event_id = rs.getInt("event_id");
				String name = rs.getString("eventname");
				int count = rs.getInt("count");
				response.getEvents().add(name);
				if (event_id == 0 && count >= MIN_COUNT) {
					response.setCanCreateEvent(true);
				}
			}
		} catch (SQLException e) {

		}
		return response;
	}

	public void createEvent(double lat, double lng, String username,
			String eventName) {

		try {
			PreparedStatement statement = usersConnection
					.prepareStatement(
							"Insert into locations(lat, lng, username, timestamp) values (?, ?, ?, ?)",
							Statement.RETURN_GENERATED_KEYS);
			statement.setDouble(1, lat);
			statement.setDouble(2, lng);
			statement.setString(3, username);
			statement
					.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
			statement.execute();
			ResultSet res = statement.getGeneratedKeys();
			long locationId = 0;
			while (res.next()) {
				locationId = res.getLong(1);
			}
			statement = usersConnection.prepareStatement(
					"Insert into event(location_id, name) values(?, ?)",
					Statement.RETURN_GENERATED_KEYS);
			statement.setLong(1, locationId);
			statement.setString(2, eventName);
			statement.execute();
			res = statement.getGeneratedKeys();
			long eventId = 0;
			while (res.next()) {
				eventId = res.getLong(1);
			}
			statement = usersConnection
					.prepareStatement("update location set event_id = ? where id=?");
			statement.setLong(1, eventId);
			statement.setLong(2, locationId);
			statement.execute();
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
	}

}
