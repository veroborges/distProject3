package edu.cmu.eventtracker.geoserver;

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
					.prepareStatement("Select host, min((maxlat-minlat) * (maxlng - minlng)) from locationshard where minlat <= ? and ? < maxlat and minlng <= ? and ? < maxlng");

			usersStatement = shardsConnection
					.prepareStatement("Select hostname, max(nodeid) from usershard where nodeid <= ? group By nodeid");

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
			rs = locationsStatement.getResultSet();
			while (rs.next()) {
				return rs.getString("host");
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

}
