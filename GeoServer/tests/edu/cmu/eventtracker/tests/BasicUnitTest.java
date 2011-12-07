/**
 * 
 */
package edu.cmu.eventtracker.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.caucho.hessian.client.HessianProxyFactory;

import edu.cmu.eventtracker.action.AddUserAction;
import edu.cmu.eventtracker.action.ClearLocationsDBAction;
import edu.cmu.eventtracker.action.ClearUsersDBAction;
import edu.cmu.eventtracker.action.CreateEventAction;
import edu.cmu.eventtracker.action.GetLocationAction;
import edu.cmu.eventtracker.action.GetUserAction;
import edu.cmu.eventtracker.action.GetUserEvents;
import edu.cmu.eventtracker.action.GetUserLocations;
import edu.cmu.eventtracker.action.InsertLocationAction;
import edu.cmu.eventtracker.action.LocationHeartbeatAction;
import edu.cmu.eventtracker.actionhandler.LocationHeartbeatHandler;
import edu.cmu.eventtracker.dto.Event;
import edu.cmu.eventtracker.dto.Location;
import edu.cmu.eventtracker.dto.LocationHeartbeatResponse;
import edu.cmu.eventtracker.dto.User;
import edu.cmu.eventtracker.geoserver.ExecuteOnMasterException;
import edu.cmu.eventtracker.geoserver.GeoServer;
import edu.cmu.eventtracker.geoserver.GeoService;
import edu.cmu.eventtracker.serverlocator.ServerLocator;
import edu.cmu.eventtracker.serverlocator.ServerLocatorCache;
import edu.cmu.eventtracker.serverlocator.ServerLocatorService;

public class BasicUnitTest {

	public static final String driver = "org.apache.derby.jdbc.EmbeddedDriver";
	public static final String protocol = "jdbc:derby:";
	private Random gen = new Random();
	private GeoServer[] servers;
	private ServerLocator serverLocator;
	private final int startPort = 9990;
	private HessianProxyFactory factory;
	private InetAddress addr;
	private ServerLocatorCache serverLocatorCache;

	@Before
	public void before() throws Exception {
		factory = new HessianProxyFactory();
		factory.setConnectTimeout(GeoService.TIMEOUT);
		factory.setReadTimeout(GeoService.TIMEOUT);
		addr = InetAddress.getLocalHost();
		startServerLocator();

	}

	@After
	public void tearDown() throws Exception {
		Thread.sleep(1000);
		if (serverLocator != null) {
			serverLocator.stop();
		}
		if (servers != null) {
			for (GeoServer server : servers) {
				server.stop();
			}
		}
	}

	@Test(timeout = GeoService.TIMEOUT)
	public void testCreateUsers() throws Exception {
		initShards();
		startGeoServers(4);

		int counter = 0;
		String username = "";
		String password = "testpass";
		String name = "testuser";
		int userNumber = 10;

		while (counter < userNumber) {
			username = "user" + gen.nextInt();
			GeoService geoService = serverLocatorCache
					.getUserShardServer(username);

			if (geoService.execute(new AddUserAction(username, name, password))) {
				counter++;
				boolean found = false;
				for (int i = 0; i < 5; i++) {
					Thread.sleep(100);
					User user = geoService.execute(new GetUserAction(username));
					if (user != null) {
						found = true;
						assertEquals(username, user.getUsername());
						break;
					}
				}
				assertTrue(found);
			}
		}
	}

	public void initShards() {
		String[] serviceUrls = getGeoServiceURLS(9990, 4);
		serverLocatorCache.addUserShard(Integer.MIN_VALUE, serviceUrls[0],
				serviceUrls[1]);
		serverLocatorCache.addUserShard(0, serviceUrls[2], serviceUrls[3]);

		serverLocatorCache.addLocationShard(-180, -180, 180, 180,
				serviceUrls[0], serviceUrls[1], "World");
		serverLocatorCache.addLocationShard(40.69418, -74.11926, 40.88237,
				-73.75122, serviceUrls[2], serviceUrls[3], "New York");
	}

	@Test(timeout = GeoService.TIMEOUT)
	public void testUserSharding() throws Exception {
		initShards();
		// figure out URL for locator service, assume DNS will take care of that
		startGeoServers(4);
		GeoService[] geoServices = getGeoServiceConnections(4);
		testInsertUser("veronica", geoServices[0]);
		testInsertUser("anar", geoServices[2]);
	}

	public void startServerLocator() throws Exception {
		serverLocator = new ServerLocator(8888);
		serverLocator.start();
		ArrayList<ServerLocatorService> locatorServices = new ArrayList<ServerLocatorService>();
		String locatorURL = ServerLocator.getURL(addr.getHostName(),
				ServerLocatorService.SERVER_LOCATOR_PORT);
		ServerLocatorService service = (ServerLocatorService) factory.create(
				ServerLocatorService.class, locatorURL);
		locatorServices.add(service);
		serverLocatorCache = new ServerLocatorCache(locatorServices);
		getServiceLocator().clearTables();
	}

	private ServerLocatorService getServiceLocator()
			throws MalformedURLException {
		return serverLocatorCache;
	}

	private void testInsertUser(String user, GeoService expectedServer)
			throws MalformedURLException {
		serverLocatorCache.getUserShardServer(user).execute(
				new AddUserAction(user, user, "pass"));
		assertEquals(user, expectedServer.execute(new GetUserAction(user))
				.getUsername());
	}

	private GeoService getGeoServiceConnection(String url)
			throws MalformedURLException {
		return (GeoService) factory.create(GeoService.class, url);
	}

	public String[] getGeoServiceURLS(int startPort, int count) {
		String[] urls = new String[count];
		for (int i = 0; i < count; i++) {
			urls[i] = GeoServer.getURL(addr.getHostName(), startPort + i);
		}
		return urls;
	}

	public GeoService[] getGeoServiceConnections(int count)
			throws MalformedURLException {
		GeoService[] services = new GeoService[count];
		for (int i = 0; i < count; i++) {
			services[i] = getGeoServiceConnection(GeoServer.getURL(
					addr.getHostName(), startPort + i));
		}
		return services;
	}

	public void startGeoServers(int count) throws Exception {
		servers = new GeoServer[count];
		for (int i = 0; i < count; i++) {
			servers[i] = new GeoServer(i + startPort, i % 2 == 0,
					ServerLocator.getURL(addr.getHostName(),
							ServerLocatorService.SERVER_LOCATOR_PORT));
			servers[i].start();
		}
		for (int i = 0; i < count; i++) {
			GeoService connection = getGeoServiceConnection(GeoServer.getURL(
					addr.getHostName(), startPort + i));
			if (i % 2 == 0) { // execute only on masters, the slaves will be
								// replicated
				connection.execute(new ClearLocationsDBAction());
				connection.execute(new ClearUsersDBAction());
			}
		}
	}

	@Test(timeout = GeoService.TIMEOUT)
	public void testLocationSharding() throws Exception {
		initShards();
		startGeoServers(4);
		double lng = -74;
		double lat = 37;
		int count = 100;
		for (int i = 0; i < count; i++) {
			lat += .2;
			serverLocatorCache.getLocationShardServer(lat, lng).execute(
					new LocationHeartbeatAction(new Location(null, lat, lng,
							"anar", null, null)));
		}
	}

	@Test(timeout = GeoService.TIMEOUT)
	public void testEventCreationOffer() throws Exception {
		initShards();
		startGeoServers(4);
		double lng = 40.8;
		double lat = -74;
		int count = LocationHeartbeatHandler.MIN_COUNT + 5;
		for (int i = 0; i < count; i++) {
			LocationHeartbeatResponse response = serverLocatorCache
					.getLocationShardServer(lat, lng).execute(
							new LocationHeartbeatAction(new Location(null, lat,
									lng, "user" + i, null, null)));
			if (i < LocationHeartbeatHandler.MIN_COUNT - 1) {
				assertFalse(response.canCreateEvent());
			} else {
				assertTrue(response.canCreateEvent());
			}
		}
	}

	@Test(timeout = GeoService.TIMEOUT)
	public void testEventCreation() throws Exception {
		initShards();
		startGeoServers(4);
		double lng = 40.8;
		double lat = -74;
		int count = LocationHeartbeatHandler.MIN_COUNT + 10;
		String eventId = null;
		int eventParticipantCount = 0;
		for (int i = 0; i < count; i++) {
			String username = "user" + i;
			LocationHeartbeatResponse response = serverLocatorCache
					.getLocationShardServer(lat, lng).execute(
							new LocationHeartbeatAction(new Location(null, lat,
									lng, username, null, null)));

			if (i < LocationHeartbeatHandler.MIN_COUNT - 1) {
				assertFalse(response.canCreateEvent());
				boolean exceptionThrown = false;
				try {
					serverLocatorCache.getLocationShardServer(lat, lng)
							.execute(
									new CreateEventAction(lat, lng, username,
											"TestEvent"));
					assert false : "this line should not be executed";
				} catch (Throwable ex) {
					// ex.printStackTrace();
					assertTrue(ex instanceof IllegalStateException);
					exceptionThrown = true;
				}
				assertTrue(exceptionThrown);
			} else if (i == LocationHeartbeatHandler.MIN_COUNT - 1) {
				assertTrue(response.canCreateEvent());
				Event event = serverLocatorCache.getLocationShardServer(lat,
						lng).execute(
						new CreateEventAction(lat, lng, username, "TestEvent"));
				assertNotNull(event);
				eventId = event.getId();
				eventParticipantCount = 1;
			} else {
				assertTrue(response.getEvents().size() == 2);
				boolean found = false;
				Event event = null;
				for (Event e : response.getEvents()) {
					if (eventId.equals(e.getId())) {
						event = e;
						break;
					}
				}
				assertNotNull(event);
				assertEquals(eventParticipantCount, event.getParticipantCount());
				found = true;
				assertTrue(found);
				LocationHeartbeatAction pingAction = new LocationHeartbeatAction(
						new Location(null, lat, lng, username, eventId, null));
				serverLocatorCache.getLocationShardServer(lat, lng).execute(
						pingAction);
				eventParticipantCount++;
			}
		}
	}

	@Test(timeout = GeoService.TIMEOUT)
	public void testFailover() throws Exception {
		initShards();
		startGeoServers(4);
		// veronica, veronica2 all map to the first server
		serverLocatorCache.getUserShardServer("veronica").execute(
				new AddUserAction("veronica", "veronica", "pass"));

		User user = null;
		for (int i = 0; i < 10; i++) {
			user = getGeoServiceConnection(
					serverLocatorCache.getUserShard("veronica").getMaster())
					.execute(new GetUserAction("veronica"));
			if (user == null) {
				Thread.sleep(100);
			} else {
				break;
			}
		}
		assertNotNull(user);
		user = null;
		for (int i = 0; i < 10; i++) {
			user = getGeoServiceConnection(
					serverLocatorCache.getUserShard("veronica").getSlave())
					.execute(new GetUserAction("veronica"));
			if (user == null) {
				Thread.sleep(100);
			} else {
				break;
			}
		}
		assertNotNull(user);
		servers[0].stop();
		serverLocatorCache.getUserShardServer("veronica2").execute(
				new AddUserAction("veronica2", "veronica2", "pass"));
		user = null;
		for (int i = 0; i < 10; i++) {
			user = getGeoServiceConnection(
					serverLocatorCache.getUserShard("veronica2").getSlave())
					.execute(new GetUserAction("veronica2"));
			if (user == null) {
				Thread.sleep(100);
			} else {
				break;
			}
		}
		assertNotNull(user);
		servers[0].start();
		for (int i = 0; i < 10; i++) {
			user = getGeoServiceConnection(
					serverLocatorCache.getUserShard("veronica2").getMaster())
					.execute(new GetUserAction("veronica2"));
			if (user == null) {
				Thread.sleep(100);
			} else {
				break;
			}
		}
		assertNotNull(user);

		ExecuteOnMasterException ex = null;
		try {
			getGeoServiceConnection(
					serverLocatorCache.getUserShard("veronica2").getSlave())
					.execute(
							new AddUserAction("veronica2", "veronica2", "pass"));
		} catch (ExecuteOnMasterException e) {
			ex = e;
		}
		assertNotNull(ex);
	}

	@Test(timeout = GeoService.TIMEOUT)
	public void testReplication() throws Exception {
		initShards();
		startGeoServers(4);
		Location location = new Location();
		location.setId(UUID.randomUUID().toString());
		location.setLat(44);
		location.setLng(44);
		location.setTimestamp(new Date());
		location.setUsername("anar");

		getGeoServiceConnection(
				serverLocatorCache.getLocationShard(44, 44).getMaster())
				.execute(new InsertLocationAction(location));
		Location found = null;
		for (int i = 0; i < 10; i++) {
			found = getGeoServiceConnection(
					serverLocatorCache.getLocationShard(44, 44).getMaster())
					.execute(new GetLocationAction(location.getId()));
			if (found != null) {
				break;
			}
			Thread.sleep(100);
		}
		assertNotNull(found);
		found = null;
		for (int i = 0; i < 10; i++) {
			found = getGeoServiceConnection(
					serverLocatorCache.getLocationShard(44, 44).getSlave())
					.execute(new GetLocationAction(location.getId()));
			if (found != null) {
				break;
			}
			Thread.sleep(100);
		}
		assertNotNull(found);
		found = null;
		for (int i = 0; i < 10; i++) {
			List<Location> locations = getGeoServiceConnection(
					serverLocatorCache.getUserShard("anar").getMaster())
					.execute(new GetUserLocations("anar"));
			if (!locations.isEmpty()) {
				found = locations.get(0);
			}
			if (found != null) {
				break;
			}
			Thread.sleep(100);
		}
		assertNotNull(found);
		found = null;
		for (int i = 0; i < 10; i++) {
			List<Location> locations = getGeoServiceConnection(
					serverLocatorCache.getUserShard("anar").getSlave())
					.execute(new GetUserLocations("anar"));
			if (!locations.isEmpty()) {
				found = locations.get(0);
			}
			if (found != null) {
				break;
			}
			Thread.sleep(100);
		}
		assertNotNull(found);
	}

	@Test(timeout = GeoService.TIMEOUT)
	public void testgetUserEvents() throws Exception {
		initShards();
		startGeoServers(4);
		double lng = 40.8;
		double lat = -74;
		int count = LocationHeartbeatHandler.MIN_COUNT + 10;
		String user = "testUser";
		int eventCounter = 0;
		ArrayList<String> evJoined = new ArrayList<String>();

		serverLocatorCache.getLocationShardServer(lat, lng).execute(
				new LocationHeartbeatAction(new Location(null, lat, lng, user,
						null, null)));

		for (int i = 0; i < count; i++) {
			String username = "user" + i;
			LocationHeartbeatResponse response = serverLocatorCache
					.getLocationShardServer(lat, lng).execute(
							new LocationHeartbeatAction(new Location(null, lat,
									lng, username, null, null)));

			if (i >= LocationHeartbeatHandler.MIN_COUNT - 1) {
				assertTrue(response.canCreateEvent());
				Event event = serverLocatorCache.getLocationShardServer(lat,
						lng).execute(
						new CreateEventAction(lat, lng, username, "TestEvent"
								+ i));

				assertFalse(event == null);

				response = serverLocatorCache.getLocationShardServer(lat, lng)
						.execute(
								new LocationHeartbeatAction(new Location(null,
										lat, lng, user, event.getId(), null)));
				eventCounter++;
				evJoined.add(event.getId());
			}

		}

		ArrayList<Event> events = (ArrayList<Event>) serverLocatorCache
				.getUserShardServer(user).execute(new GetUserEvents(user));

		assertTrue(events.size() == eventCounter);

		for (Event e : events) {
			assertTrue(evJoined.contains(e.getId()));
		}
	}
}
