package edu.cmu.eventtracker.serverlocator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;

import com.caucho.hessian.server.HessianServlet;

public class ServerLocatorServiceImpl extends HessianServlet
		implements
			ServerLocatorService {
	public final String driver = "org.apache.derby.jdbc.EmbeddedDriver";
	public final String protocol = "jdbc:derby:";
	public PreparedStatement locationsStatement;
	public PreparedStatement usersStatement;
	public PreparedStatement usersMaxStatement;

	private Connection shardsConnection;

	public ServerLocatorServiceImpl() {

	}

	@Override
	public void init() throws ServletException {
		super.init();
		int port = (Integer) this.getServletContext().getAttribute("PORT");
		try {
			shardsConnection = DriverManager.getConnection(protocol
					+ "shardsDB" + port + ";create=true", null);
			locationsStatement = shardsConnection
					.prepareStatement("Select hostname, min((latmax-latmin) * (lngmax - lngmin)) from locationshard where latmin <= ? and ? < latmax and lngmin <= ? and ? < lngmax group by hostname, lngmin, lngmax, latmax, latmin");

			usersStatement = shardsConnection
					.prepareStatement("select hostname from usershard join (Select max(nodeid) maxnode from usershard where nodeid <= ?) s on usershard.nodeid = maxnode");

			usersMaxStatement = shardsConnection
					.prepareStatement("Select max(nodeid) from usershard");

		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public String getUserShard(String username) {
		ResultSet rs = null;

		try {
			int hash = username.hashCode();

			usersStatement.setInt(1, hash);
			usersStatement.execute();
			rs = usersStatement.getResultSet();

			if (rs.next()) {
				return rs.getString("hostname");
			} else {
				usersMaxStatement.execute();

				if (rs.next()) {
					return rs.getString("hostname");
				}
			}
			return null;

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

	@Override
	public String getLocationShard(double lat, double lng) {
		ResultSet rs = null;
		try {
			locationsStatement.setDouble(1, lat);
			locationsStatement.setDouble(2, lat);
			locationsStatement.setDouble(3, lng);
			locationsStatement.setDouble(4, lng);
			locationsStatement.execute();
			locationsStatement.setMaxRows(1);
			rs = locationsStatement.getResultSet();
			while (rs.next()) {
				return rs.getString("hostname");
			}
			return null;
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

	public void addUserShard(int nodeid, String hostname) {
		try {
			PreparedStatement createShard = shardsConnection
					.prepareStatement("insert into usershard values(?, ?) ");

			createShard.setInt(1, nodeid);
			createShard.setString(2, hostname);
			createShard.execute();

		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
	}

	public void addLocationShard(double latmin, double lngmin, double latmax,
			double lngmax, String hostname, String name) {
		try {
			PreparedStatement createShard = shardsConnection
					.prepareStatement("insert into locationshard(latmin, lngmin, latmax, lngmax, hostname, name) values(?, ?, ?, ?, ?, ?) ");

			createShard.setDouble(1, latmin);
			createShard.setDouble(2, lngmin);
			createShard.setDouble(3, latmax);
			createShard.setDouble(4, lngmax);
			createShard.setString(5, hostname);
			createShard.setString(6, name);
			createShard.execute();
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
	}

	public void clearTables() {
		try {
			shardsConnection.prepareStatement("Delete from locationshard")
					.execute();
			shardsConnection.prepareStatement("Delete from usershard")
					.execute();
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
	}
}