/**
 * 
 */
package edu.cmu.eventtracker.tests;

import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.util.Random;
import org.junit.Test;

import com.caucho.hessian.client.HessianProxyFactory;

import edu.cmu.eventtracker.geoserver.GeoService;
import edu.cmu.eventtracker.geoserver.ServerLocatorService;

public class BasicUnitTest {

	public String driver = "org.apache.derby.jdbc.EmbeddedDriver";
	public String protocol = "jdbc:derby:";
	Random gen = new Random();

	public void testCreateUsers(int userNumber) {
		String url = "http://localhost:9991/";

		int counter = 0;
		String username = "";
		String password = "testpass";
		String name = "testuser";

		while (counter < userNumber) {
			username = Integer.toString(gen.nextInt())
					+ Integer.toString(gen.nextInt());
			try {
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

			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
	}

	@Test
	public void testSharding() {

	}
}
