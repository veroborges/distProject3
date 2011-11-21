package edu.cmu.eventtracker.geoserver;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class ServerLocator {

	public String driver = "org.apache.derby.jdbc.EmbeddedDriver";
	public String protocol = "jdbc:derby:";

	public ServerLocator(int port, String jdbc) {
		try {

			Server server = new Server(port);
			ServletContextHandler context = new ServletContextHandler(server,
					"/", ServletContextHandler.SESSIONS);
			context.addServlet(new ServletHolder(GeoServiceImpl.class), "/"
					+ GeoService.class.getSimpleName());
			server.setHandler(context);
			server.start();
			Class.forName(driver).newInstance();
			try {
				initShardsDB();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		} catch (Throwable e) {
			throw new IllegalStateException(e);
		}
	}
	
	private void initShardsDB() throws SQLException {
		Connection conn = DriverManager.getConnection(protocol
				+ "shardsDB;create=true", null);
		Statement statement = conn.createStatement();
		statement
				.execute("CREATE TABLE LOCATIONSHARD (LNGMAX FLOAT NOT NULL,LATMIN FLOAT NOT NULL,LNGMIN FLOAT NOT NULL, LATMAX FLOAT NOT NULL, HOSTNAME varchar(255) NOT NULL,PRIMARY KEY (LNGMAX,LATMIN,LNGMIN,LATMAX))");
		statement
				.execute("CREATE TABLE USERSHARD (NODEID INTEGER NOT NULL, HOSTNAME varchar(255) NOT NULL,  PRIMARY KEY (NODEID))");
		conn.close();
	}

	public static void main(String[] args) {
		new ServerLocator(9991, "");
	}

}
