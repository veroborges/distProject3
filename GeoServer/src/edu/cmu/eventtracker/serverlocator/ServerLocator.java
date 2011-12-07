package edu.cmu.eventtracker.serverlocator;

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
	private int port;
	private Server server;

	public ServerLocator(int port) {
		this.port = port;
		try {

			server = new Server(port);
			ServletContextHandler context = new ServletContextHandler(server,
					"/", ServletContextHandler.SESSIONS);
			context.addServlet(
					new ServletHolder(ServerLocatorServiceImpl.class), "/"
							+ ServerLocatorService.class.getSimpleName());
			context.setAttribute("PORT", port);
			server.setHandler(context);
			Class.forName(driver).newInstance();
			try {
				initShardsDB();
			} catch (SQLException ex) {
				// ex.printStackTrace();
			}
		} catch (Throwable e) {
			throw new IllegalStateException(e);
		}
	}

	private void initShardsDB() throws SQLException {
		Connection conn = DriverManager.getConnection(protocol + "shardsDB"
				+ port + ";create=true", null);
		Statement statement = conn.createStatement();
		statement
				.execute("CREATE TABLE LOCATIONSHARD (LNGMAX FLOAT NOT NULL,LATMIN FLOAT NOT NULL,LNGMIN FLOAT NOT NULL, LATMAX FLOAT NOT NULL, MASTER varchar(255) NOT NULL, SLAVE varchar(255) NOT NULL, NAME varchar(255),PRIMARY KEY (LNGMAX,LATMIN,LNGMIN,LATMAX))");
		statement
				.execute("CREATE TABLE USERSHARD (NODEID INTEGER NOT NULL, MASTER varchar(255) NOT NULL, SLAVE varchar(255) NOT NULL, PRIMARY KEY (NODEID))");
		conn.close();
	}

	public static void main(String[] args) {
		try {
			new ServerLocator(ServerLocatorService.START_PORT).start();
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
			DriverManager.getConnection(protocol + "shardsDB" + port
					+ ";shutdown=true");
		} catch (SQLException e) {

		}
	}

	public static String getURL(String hostname, int port) {
		return "http://" + hostname + ":" + port + "/"
				+ ServerLocatorService.class.getSimpleName();
	}

}
