package edu.cmu.eventtracker.geoserverclient;

import java.net.MalformedURLException;

import com.caucho.hessian.client.HessianProxyFactory;

import edu.cmu.eventtracker.geoserver.GeoService;
import edu.cmu.eventtracker.serverlocator.ServerLocatorService;

public class GeoServerClient {

	public static void main(String[] args) throws MalformedURLException {
		
		//figure out URL for locator service, assume DNS will take care of that
		String url = "http://localhost:9991/";

		HessianProxyFactory factory = new HessianProxyFactory();
		ServerLocatorService locatorService = (ServerLocatorService) factory.create(ServerLocatorService.class, url + ServerLocatorService.class.getSimpleName());
		GeoService geoService = (GeoService) factory.create(GeoService.class, locatorService.getUserShard("testuser") + GeoService.class.getSimpleName());
		geoService.getUserLocations("testuser");
		geoService.getUserEvents("testuser");
		
	}
	
}
