package edu.cmu.eventtracker.geoserverclient;

import java.net.MalformedURLException;

import com.caucho.hessian.client.HessianProxyFactory;

import edu.cmu.eventtracker.geoserver.GeoService;

public class GeoServerClient {

	public static void main(String[] args) throws MalformedURLException {
		String url = "http://localhost:9999/GeoService";

		HessianProxyFactory factory = new HessianProxyFactory();
		GeoService basic = (GeoService) factory.create(GeoService.class, url);

		System.out.println("Hello: " + basic.hello());
	}
}
