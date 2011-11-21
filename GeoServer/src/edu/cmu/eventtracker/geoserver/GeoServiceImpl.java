package edu.cmu.eventtracker.geoserver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import edu.cmu.eventtracker.geoserver.Location;

import com.caucho.hessian.server.HessianServlet;

public class GeoServiceImpl extends HessianServlet implements GeoService {
	public final String driver = "org.apache.derby.jdbc.EmbeddedDriver";
	public final String protocol = "jdbc:derby:";

	private final Connection usersConnection;
	private final Connection locationsConnection;
	public final PreparedStatement userLocationsStatement;
	public final PreparedStatement userEventsStatement;

	
	public GeoServiceImpl() {
		try {
			usersConnection = DriverManager.getConnection(protocol
					+ "usersDB;create=true", null);
			locationsConnection = DriverManager.getConnection(protocol
					+ "locationsDB;create=true", null);
			
			//statement to get all of a user's locations
			userLocationsStatement = usersConnection
					.prepareStatement("select lat, lng, event_id from location where username= ?");
			
			//statement to get all of a user's events
			userEventsStatement = usersConnection
					.prepareStatement("select name from event where id in (select eventid from userevent where username= ?)");
		
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public List<Location> getUserLocations(String username) {
		List<Location> locations = new ArrayList<Location>();
		ResultSet rs = null;
		try{
			userLocationsStatement.setString(1, username);
			rs = userLocationsStatement.getResultSet();
			
			//populate location lists with rows returned
			while(rs.next()){
				//create location object
				Location loc = new Location();
				loc.setLat(rs.getFloat("lat"));
				loc.setLng(rs.getFloat("lng"));
				loc.setUsername(username);
				loc.setEvent(rs.getInt("event_id"));
				locations.add(loc);
			}
			
			return locations;
			
		}catch (SQLException e) {
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
	
	@Override
	public List<String> getUserEvents(String username) {
		List<String> events = new ArrayList<String>();
		ResultSet rs = null;
		
		try{
			userEventsStatement.setString(1, username);
			rs = userEventsStatement.getResultSet();
			
			//popule events list with eventnames returned
			while(rs.next()){
				events.add(rs.getString("name"));
			}
			
			return events;
			
		}catch (SQLException e) {
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
