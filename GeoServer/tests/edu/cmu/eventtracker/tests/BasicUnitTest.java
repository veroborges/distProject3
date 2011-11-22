/**
 * 
 */
package edu.cmu.eventtracker.tests;

import static org.junit.Assert.*;

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
	public void testSharding() throws MalformedURLException {
		// figure out URL for locator service, assume DNS will take care of that
		String geo1 = "http://localhost:9990/";
		String geo2 = "http://localhost:9991/";
		String locatorURL = "http://localhost:9995/";

		GeoServer server1 = new GeoServer(9990);
		GeoServer server2 = new GeoServer(9991);
		ServerLocator locator = new ServerLocator(9995);

		HessianProxyFactory factory = new HessianProxyFactory();
		ServerLocatorService locatorService = (ServerLocatorService) factory
				.create(ServerLocatorService.class, locatorURL
						+ ServerLocatorService.class.getSimpleName());

		locatorService.addUserShard(Integer.MIN_VALUE, geo1);
		locatorService.addUserShard(0, geo2);

		GeoService geoService = (GeoService) factory.create(
				GeoService.class,
				locatorService.getUserShard("veronica")
						+ GeoService.class.getSimpleName());
		
	
		geoService.addUser("veronica", "Vero", "1234");
		geoService.addUser("anar", "Anar", "1234");
		
	}
}
