package edu.cmu.eventtracker.geoserverclient;

import java.net.MalformedURLException;

import com.caucho.hessian.client.HessianProxyFactory;

import edu.cmu.eventtracker.action.LocationHeartbeatAction;
import edu.cmu.eventtracker.dto.Location;
import edu.cmu.eventtracker.geoserver.GeoService;
import edu.cmu.eventtracker.serverlocator.ServerLocator;
import edu.cmu.eventtracker.serverlocator.ServerLocatorService;

public class GeoServerClient {

	public static void main(String[] args) throws MalformedURLException {
		
		//figure out URL for locator service, assume DNS will take care of that
		String url = "http://localhost:" + ServerLocatorService.START_PORT + "/" + ServerLocatorService.class.getSimpleName(); 

		HessianProxyFactory factory = new HessianProxyFactory();
		factory.setConnectTimeout(GeoService.TIMEOUT);
		factory.setReadTimeout(GeoService.TIMEOUT);
		ServerLocatorService locatorService = (ServerLocatorService) factory.create(ServerLocatorService.class, url);
		String master = locatorService.getUserShard("testuser").getMaster();
		GeoService geoService = (GeoService) factory.create(GeoService.class, master);
		geoService.execute(new LocationHeartbeatAction(new Location()));
		
		
	}
	
}
