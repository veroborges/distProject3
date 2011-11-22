/**
 * 
 */
package edu.cmu.eventtracker.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.MalformedURLException;
import java.util.Random;

import org.junit.Test;

import com.caucho.hessian.client.HessianProxyFactory;

import edu.cmu.eventtracker.geoserver.GeoServer;
import edu.cmu.eventtracker.geoserver.GeoService;
import edu.cmu.eventtracker.geoserver.ServerLocator;
import edu.cmu.eventtracker.geoserver.ServerLocatorService;

public class BasicUnitTest {

	public String driver = "org.apache.derby.jdbc.EmbeddedDriver";
	public String protocol = "jdbc:derby:";
	Random gen = new Random();

	public void testCreateUsers(int userNumber) throws MalformedURLException {
		String url = "http://localhost:9991/";

		int counter = 0;
		String username = "";
		String password = "testpass";
		String name = "testuser";

		while (counter < userNumber) {
			username = Integer.toString(gen.nextInt())
					+ Integer.toString(gen.nextInt());
			HessianProxyFactory factory = new HessianProxyFactory();
			ServerLocatorService locatorService = (ServerLocatorService) factory
					.create(ServerLocatorService.class, url
							+ ServerLocatorService.class.getSimpleName());
			GeoService geoService = (GeoService) factory.create(
					GeoService.class, locatorService.getUserShard(username)
							+ GeoService.class.getSimpleName());

			if (geoService.addUser(username, name, password)) {
				counter++;
			}

		}
	}

	@Test
	public void testUserSharding() throws MalformedURLException {
		// figure out URL for locator service, assume DNS will take care of that

		String locatorURL = "http://localhost:8888/";

		new ServerLocator(8888);
		startGeoServers(9990, 2);
		HessianProxyFactory factory = new HessianProxyFactory();
		ServerLocatorService locatorService = (ServerLocatorService) factory
				.create(ServerLocatorService.class, locatorURL
						+ ServerLocatorService.class.getSimpleName());
		locatorService.clearTables();
		String[] serviceUrls = getGeoServiceURLS(9990, 2);
		GeoService[] geoServices = getGeoServiceConnections(factory, 9990, 2);
		geoServices[0].clearUserDB();
		geoServices[1].clearUserDB();
		locatorService.addUserShard(Integer.MIN_VALUE, serviceUrls[0]);
		locatorService.addUserShard(0, serviceUrls[1]);

		testInsertUser(factory, locatorService, "veronica", geoServices[0]);
		testInsertUser(factory, locatorService, "anar", geoServices[1]);

	}

	private void testInsertUser(HessianProxyFactory factory,
			ServerLocatorService locatorService, String user,
			GeoService expectedServer) throws MalformedURLException {
		getGeoServiceConnection(factory, locatorService.getUserShard(user))
				.addUser(user, user, "pass");
		assertNotNull(expectedServer.getUser(user));
		assertEquals(user, expectedServer.getUser(user).getUsername());
	}

	public String[] getGeoServiceURLS(int startPort, int count) {
		String[] urls = new String[count];
		for (int i = 0; i < count; i++) {
			urls[i] = "http://localhost:" + (startPort + i) + "/"
					+ GeoService.class.getSimpleName();
		}
		return urls;
	}

	public GeoService[] getGeoServiceConnections(HessianProxyFactory factory,
			int startPort, int count) throws MalformedURLException {
		GeoService[] services = new GeoService[count];
		for (int i = 0; i < count; i++) {
			services[i] = getGeoServiceConnection(factory, "http://localhost:"
					+ (startPort + i) + "/" + GeoService.class.getSimpleName());
		}
		return services;
	}

	public GeoService getGeoServiceConnection(HessianProxyFactory factory,
			String url) throws MalformedURLException {
		return (GeoService) factory.create(GeoService.class, url);
	}

	public GeoServer[] startGeoServers(int startPort, int count) {
		GeoServer[] servers = new GeoServer[count];
		for (int i = 0; i < count; i++) {
			servers[i] = new GeoServer(i + startPort);
		}
		return servers;
	}
}
