package edu.cmu.eventtracker.geoserver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import com.caucho.hessian.server.HessianServlet;

public class GeoServiceImpl extends HessianServlet implements GeoService {
	public final String driver = "org.apache.derby.jdbc.EmbeddedDriver";
	public final String protocol = "jdbc:derby:";

	private final Connection usersConnection;
	private final Connection locationsConnection;
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

}
