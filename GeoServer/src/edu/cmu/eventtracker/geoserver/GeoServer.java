package edu.cmu.eventtracker.geoserver;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class GeoServer {

	public String driver = "org.apache.derby.jdbc.EmbeddedDriver";
	public String protocol = "jdbc:derby:";
	private int port;
	
	public GeoServer(int port, String jdbc) {
		this.port = port;
		try {

			Server server = new Server(port);
			ServletContextHandler context = new ServletContextHandler(server,
					"/", ServletContextHandler.SESSIONS);
			context.addServlet(new ServletHolder(GeoServiceImpl.class), "/"
					+ GeoService.class.getSimpleName());
			context.setAttribute("PORT", port);
			server.setHandler(context);
			server.start();
			Class.forName(driver).newInstance();
			try {
				initUsersDB();
			} catch (SQLException ex) {
				//ex.printStackTrace();
			}
			try {
				initLocationsDB();
			} catch (SQLException ex) {
				//ex.printStackTrace();
			}
		} catch (Throwable e) {
			throw new IllegalStateException(e);
		}
	}

	private void initUsersDB() throws SQLException {
		Connection conn = DriverManager.getConnection(protocol
				+ "usersDB" + port +";create=true", null);
		Statement statement = conn.createStatement();
		statement
				.execute("CREATE TABLE LOCATION (  ID bigint not null GENERATED ALWAYS AS IDENTITY,  LAT float not NULL,  LNG float not NULL,  TIMESTAMP timestamp not null,  USERNAME varchar(255) not NULL,  EVENT_id bigint DEFAULT NULL,  PRIMARY KEY (ID))");
		statement
				.execute("CREATE TABLE EVENT (id bigint NOT NULL GENERATED ALWAYS AS IDENTITY, NAME varchar(255) DEFAULT NULL, TIMESTAMP timestamp DEFAULT NULL, LOCATION_ID bigint DEFAULT NULL, PRIMARY KEY (id))");

		statement
				.execute("CREATE TABLE USERS (USERNAME varchar(255) NOT NULL, NAME varchar(255) DEFAULT NULL, PASSWORD varchar(255) DEFAULT NULL,  PRIMARY KEY (USERNAME))");
		statement
				.execute("CREATE TABLE USEREVENT ( USERNAME varchar(255) NOT NULL, EVENTID bigint NOT NULL, PRIMARY KEY (USERNAME,EVENTID),   CONSTRAINT userevent_ibfk_1 FOREIGN KEY (EVENTID) REFERENCES EVENT (id))");
		statement
				.execute("ALTER TABLE EVENT ADD CONSTRAINT FK_EVENT_LOCATION_ID FOREIGN KEY (LOCATION_ID) REFERENCES LOCATION (ID)");
		statement
				.execute("ALTER TABLE LOCATION ADD CONSTRAINT FK_LOCATION_EVENT_id FOREIGN KEY (EVENT_id) REFERENCES EVENT (id)");
		conn.close();
	}
	private void initLocationsDB() throws SQLException {
		Connection conn = DriverManager.getConnection(protocol
				+ "locationsDB" + port +";create=true", null);
		Statement statement = conn.createStatement();
		statement
				.execute("CREATE TABLE LOCATION (  ID bigint not null GENERATED ALWAYS AS IDENTITY,  LAT float not NULL,  LNG float not NULL,  TIMESTAMP timestamp not null,  USERNAME varchar(255) not NULL,  EVENT_id bigint DEFAULT NULL,  PRIMARY KEY (ID))");
		statement
				.execute("CREATE TABLE EVENT (id bigint NOT NULL GENERATED ALWAYS AS IDENTITY, NAME varchar(255) DEFAULT NULL, TIMESTAMP timestamp DEFAULT NULL, LOCATION_ID bigint DEFAULT NULL, PRIMARY KEY (id))");

		// statement
		// .execute("CREATE TABLE USERS (USERNAME varchar(255) NOT NULL, NAME varchar(255) DEFAULT NULL, PASSWORD varchar(255) DEFAULT NULL,  PRIMARY KEY (USERNAME))");
		statement
				.execute("CREATE TABLE USEREVENT ( USERNAME varchar(255) NOT NULL, EVENTID bigint NOT NULL, PRIMARY KEY (USERNAME,EVENTID),   CONSTRAINT userevent_ibfk_1 FOREIGN KEY (EVENTID) REFERENCES EVENT (id))");
		statement
				.execute("ALTER TABLE EVENT ADD CONSTRAINT FK_EVENT_LOCATION_ID FOREIGN KEY (LOCATION_ID) REFERENCES LOCATION (ID)");
		statement
				.execute("ALTER TABLE LOCATION ADD CONSTRAINT FK_LOCATION_EVENT_id FOREIGN KEY (EVENT_id) REFERENCES EVENT (id)");
		conn.close();
	}

	public static void main(String[] args) {
		new GeoServer(9990, "");
	}

}
