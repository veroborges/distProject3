package edu.cmu.eventtracker.geoserver;

import java.net.InetAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.caucho.hessian.client.HessianProxyFactory;

import edu.cmu.eventtracker.serverlocator.ServerLocator;
import edu.cmu.eventtracker.serverlocator.ServerLocatorCache;
import edu.cmu.eventtracker.serverlocator.ServerLocatorService;

public class GeoServer {

	public String driver = "org.apache.derby.jdbc.EmbeddedDriver";
	public String protocol = "jdbc:derby:";
	private int port;
	private Server server;
	static {
		Logger.getLogger("GeoServer").setLevel(Level.FINE);
	}

	public GeoServer(int port, boolean master, ServerLocatorCache serverLocator) {
		this.port = port;
		try {

			server = new Server(port);
			ServletContextHandler context = new ServletContextHandler(server,
					"/", ServletContextHandler.SESSIONS);
			context.addServlet(new ServletHolder(GeoServiceImpl.class), "/"
					+ GeoService.class.getSimpleName());
			context.setAttribute("PORT", port);
			context.setAttribute("MASTER", master);
			context.setAttribute("SERVER_LOCATOR", serverLocator);
			server.setHandler(context);
			Class.forName(driver).newInstance();
			try {
				initUsersDB();
			} catch (SQLException ex) {
				// ex.printStackTrace();
			}
			try {
				initLocationsDB();
			} catch (SQLException ex) {
				// ex.printStackTrace();
			}
		} catch (Throwable e) {
			throw new IllegalStateException(e);
		}
	}

	private void initUsersDB() throws SQLException {
		Connection conn = DriverManager.getConnection(protocol + "usersDB"
				+ port + ";create=true", null);
		Statement statement = conn.createStatement();
		// statement
		// .execute("CREATE TABLE EVENT (id CHAR(36) not null, NAME varchar(255) DEFAULT NULL, TIMESTAMP timestamp DEFAULT NULL, PRIMARY KEY (id))");

		statement
				.execute("CREATE TABLE LOCATION (  ID CHAR(36) not null,  LAT float not NULL,  LNG float not NULL,  TIMESTAMP timestamp not null,  USERNAME varchar(255) not NULL,  EVENT_ID CHAR(36) DEFAULT NULL,  PRIMARY KEY (ID)) ");

		statement
				.execute("CREATE TABLE USERS (USERNAME varchar(255) NOT NULL, NAME varchar(255) DEFAULT NULL, PASSWORD varchar(255) DEFAULT NULL,  PRIMARY KEY (USERNAME))");

		conn.close();
	}

	private void initLocationsDB() throws SQLException {
		Connection conn = DriverManager.getConnection(protocol + "locationsDB"
				+ port + ";create=true", null);
		Statement statement = conn.createStatement();
		statement
				.execute("CREATE TABLE EVENT (id CHAR(36) not null, NAME varchar(255) DEFAULT NULL, PRIMARY KEY (id))");

		statement
				.execute("CREATE TABLE LOCATION (  ID CHAR(36) not null,  LAT float not NULL,  LNG float not NULL,  TIMESTAMP timestamp not null,  USERNAME varchar(255) not NULL,  EVENT_ID CHAR(36) DEFAULT NULL,  PRIMARY KEY (ID)) ");

		// statement
		// .execute("CREATE TABLE USERS (USERNAME varchar(255) NOT NULL, NAME varchar(255) DEFAULT NULL, PASSWORD varchar(255) DEFAULT NULL,  PRIMARY KEY (USERNAME))");

		conn.close();
	}

	public static void main(String[] args) {

		try {

			HessianProxyFactory factory = new HessianProxyFactory();
			factory.setConnectTimeout(GeoService.TIMEOUT);
			factory.setReadTimeout(GeoService.TIMEOUT);

			ArrayList<ServerLocatorService> services = new ArrayList<ServerLocatorService>();

			ServerLocatorService service = (ServerLocatorService) factory
					.create(ServerLocatorService.class, ServerLocator.getURL(
							InetAddress.getLocalHost().getHostName(),
							ServerLocatorService.START_PORT));
			services.add(service);
			new GeoServer(GeoService.START_PORT, true, new ServerLocatorCache(
					services)).start();
		} catch (Throwable e) {
			throw new IllegalStateException(e);
		}
	}
	public void start() throws Exception {
		server.start();
	}

	public void stop() throws Exception {
		server.stop();
		try {
			DriverManager.getConnection(protocol + "locationsDB" + port
					+ ";shutdown=true");
			DriverManager.getConnection(protocol + "usersDB" + port
					+ ";shutdown=true");
		} catch (SQLException e) {

		}
	}

	public static String getURL(String hostname, int port) {
		return "http://" + hostname + ":" + port + "/"
				+ GeoService.class.getSimpleName();
	}

}
