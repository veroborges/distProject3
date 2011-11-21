package edu.cmu.eventtracker.geoserver;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class GeoServer {

	public GeoServer(int port, String jdbc) {
		try {

			Server server = new Server(port);
			ServletContextHandler context = new ServletContextHandler(server,
					"/", ServletContextHandler.SESSIONS);
			context.addServlet(new ServletHolder(GeoServiceImpl.class), "/"
					+ GeoService.class.getSimpleName());
			server.setHandler(context);
			server.start();
		} catch (Throwable e) {
			throw new IllegalStateException(e);
		}
	}
	

	
	public static void main(String[] args) {
		new GeoServer(9999, "");
	}

}
