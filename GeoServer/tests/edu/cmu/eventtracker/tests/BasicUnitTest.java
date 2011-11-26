/**
 * 
 */
package edu.cmu.eventtracker.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.util.Map.Entry;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.caucho.hessian.client.HessianProxyFactory;

import edu.cmu.eventtracker.geoserver.GeoServer;
import edu.cmu.eventtracker.geoserver.GeoService;
import edu.cmu.eventtracker.geoserver.action.AddUserAction;
import edu.cmu.eventtracker.geoserver.action.ClearLocationsDBAction;
import edu.cmu.eventtracker.geoserver.action.ClearUsersDBAction;
import edu.cmu.eventtracker.geoserver.action.CreateEventAction;
import edu.cmu.eventtracker.geoserver.action.GetUserAction;
import edu.cmu.eventtracker.geoserver.action.PingAction;
import edu.cmu.eventtracker.geoserver.actionhandler.PingHandler;
import edu.cmu.eventtracker.geoserver.dto.Event;
import edu.cmu.eventtracker.geoserver.dto.PingResponse;
import edu.cmu.eventtracker.serverlocator.ServerLocator;
import edu.cmu.eventtracker.serverlocator.ServerLocatorService;

public class BasicUnitTest {

	public String driver = "org.apache.derby.jdbc.EmbeddedDriver";
	public String protocol = "jdbc:derby:";
	private Random gen = new Random();
	private GeoServer[] servers;
	private ServerLocator serverLocator;
	private HessianProxyFactory factory;
	private final int startPort = 9990;

	@Before
	public void before() throws Exception {
		factory = new HessianProxyFactory();
		startServerLocator();
	}

	@After
	public void tearDown() throws Exception {
		if (serverLocator != null) {
			serverLocator.stop();
		}
		if (servers != null) {
			for (GeoServer server : servers) {
				server.stop();
			}
		}
	}

	public void initUserShards(ServerLocatorService locationService) {
		String[] serviceUrls = getGeoServiceURLS(9990, 2);
		locationService.addUserShard(Integer.MIN_VALUE, serviceUrls[0]);
		locationService.addUserShard(0, serviceUrls[1]);
	}

	@Test(timeout = 5000)
	public void testCreateUsers() throws Exception {
		ServerLocatorService serviceLocator = getServiceLocator();
		initUserShards(serviceLocator);
		startGeoServers(2);

		int counter = 0;
		String username = "";
		String password = "testpass";
		String name = "testuser";
		int userNumber = 50;

		while (counter < userNumber) {
			username = "user" + gen.nextInt();
			GeoService geoService = (GeoService) factory.create(
					GeoService.class, serviceLocator.getUserShard(username));

			if (geoService.execute(new AddUserAction(username, name, password))) {
				counter++;
				assertEquals(username,
						geoService.execute(new GetUserAction(username))
								.getUsername());
			}
		}
	}

	@Test
	public void testUserSharding() throws Exception {
		// figure out URL for locator service, assume DNS will take care of that
		startGeoServers(2);
		ServerLocatorService locatorService = getServiceLocator();
		initUserShards(locatorService);
		GeoService[] geoServices = getGeoServiceConnections(2);
		testInsertUser(factory, locatorService, "veronica", geoServices[0]);
		testInsertUser(factory, locatorService, "anar", geoServices[1]);
	}

	private void startServerLocator() throws Exception {
		serverLocator = new ServerLocator(8888);
		serverLocator.start();
		getServiceLocator().clearTables();
	}

	private ServerLocatorService getServiceLocator()
			throws MalformedURLException {
		String locatorURL = "http://localhost:8888/";
		return (ServerLocatorService) factory.create(
				ServerLocatorService.class, locatorURL
						+ ServerLocatorService.class.getSimpleName());
	}

	private void testInsertUser(HessianProxyFactory factory,
			ServerLocatorService locatorService, String user,
			GeoService expectedServer) throws MalformedURLException {
		getGeoServiceConnection(locatorService.getUserShard(user)).execute(
				new AddUserAction(user, user, "pass"));
		assertEquals(user, expectedServer.execute(new GetUserAction(user))
				.getUsername());
	}

	public String[] getGeoServiceURLS(int startPort, int count) {
		String[] urls = new String[count];
		for (int i = 0; i < count; i++) {
			urls[i] = "http://localhost:" + (startPort + i) + "/"
					+ GeoService.class.getSimpleName();
		}
		return urls;
	}

	public GeoService[] getGeoServiceConnections(int count)
			throws MalformedURLException {
		GeoService[] services = new GeoService[count];
		for (int i = 0; i < count; i++) {
			services[i] = getGeoServiceConnection("http://localhost:"
					+ (startPort + i) + "/" + GeoService.class.getSimpleName());
		}
		return services;
	}

	public GeoService getGeoServiceConnection(String url)
			throws MalformedURLException {
		return (GeoService) factory.create(GeoService.class, url);
	}

	public void startGeoServers(int count) throws Exception {
		servers = new GeoServer[count];
		for (int i = 0; i < count; i++) {
			servers[i] = new GeoServer(i + startPort);
			servers[i].start();
			GeoService connection = getGeoServiceConnection("http://localhost:"
					+ (startPort + i) + "/" + GeoService.class.getSimpleName());
			connection.execute(new ClearLocationsDBAction());
			connection.execute(new ClearUsersDBAction());
		}
	}

	@Test(timeout = 5000)
	public void testLocationSharding() throws Exception {
		startGeoServers(2);
		ServerLocatorService locatorService = getServiceLocator();
		initLocationShards(locatorService);
		double lng = -74;
		double lat = 37;
		int count = 100;
		for (int i = 0; i < count; i++) {
			lat += .2;
			getGeoServiceConnection(locatorService.getLocationShard(lat, lng))
					.execute(new PingAction(lat, lng, "anar"));
		}
	}

	public void initLocationShards(ServerLocatorService locationService) {
		String[] serviceUrls = getGeoServiceURLS(9990, 2);
		locationService.addLocationShard(-180, -180, 180, 180, serviceUrls[0],
				"World");
		locationService.addLocationShard(40.69418, -74.11926, 40.88237,
				-73.75122, serviceUrls[1], "New York");
	}

	@Test
	// (timeout = 5000)
	public void testEventCreationOffer() throws Exception {
		startGeoServers(2);
		ServerLocatorService locatorService = getServiceLocator();
		initLocationShards(locatorService);
		double lng = 40.8;
		double lat = -74;
		int count = PingHandler.MIN_COUNT + 5;
		for (int i = 0; i < count; i++) {
			PingResponse response = getGeoServiceConnection(
					locatorService.getLocationShard(lat, lng)).execute(
					new PingAction(lat, lng, "user" + i));
			if (i < PingHandler.MIN_COUNT - 1) {
				assertFalse(response.canCreateEvent());
			} else {
				assertTrue(response.canCreateEvent());
			}
		}
	}

	@Test
	// (timeout = 5000)
	public void testEventCreation() throws Exception {
		startGeoServers(2);
		ServerLocatorService locatorService = getServiceLocator();
		initLocationShards(locatorService);
		double lng = 40.8;
		double lat = -74;
		int count = PingHandler.MIN_COUNT + 10;
		long eventId = 0;
		int eventParticipantCount = 0;
		for (int i = 0; i < count; i++) {
			String username = "user" + i;
			PingResponse response = getGeoServiceConnection(
					locatorService.getLocationShard(lat, lng)).execute(
					new PingAction(lat, lng, username));

			if (i < PingHandler.MIN_COUNT - 1) {
				assertFalse(response.canCreateEvent());
				boolean exceptionThrown = false;
				try {
					getGeoServiceConnection(
							locatorService.getLocationShard(lat, lng)).execute(
							new CreateEventAction(lat, lng, username,
									"TestEvent"));
					assert false : "this line should not be executed";
				} catch (Throwable ex) {
					assertTrue(ex instanceof IllegalStateException);
					exceptionThrown = true;
				}
				assertTrue(exceptionThrown);
			} else if (i == PingHandler.MIN_COUNT - 1) {
				assertTrue(response.canCreateEvent());
				Event event = getGeoServiceConnection(
						locatorService.getLocationShard(lat, lng)).execute(
						new CreateEventAction(lat, lng, username, "TestEvent"));
				assertNotNull(event);
				eventId = event.getId();
				eventParticipantCount = 1;
			} else {
				assertTrue(response.getEvents().size() == 2);
				boolean found = false;
				for (Entry<Event, Integer> entry : response.getEvents()
						.entrySet()) {
					if (entry.getKey() != null) {
						assertEquals(entry.getKey().getId(), eventId);
						assertEquals(eventParticipantCount,
								(int) entry.getValue());
						found = true;
					}
				}
				assertTrue(found);
				PingAction pingAction = new PingAction(lat, lng, username);
				pingAction.setEventId(eventId);
				getGeoServiceConnection(
						locatorService.getLocationShard(lat, lng)).execute(
						pingAction);
				eventParticipantCount++;
			}
		}
	}
}
