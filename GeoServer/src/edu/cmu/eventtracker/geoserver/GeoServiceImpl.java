package edu.cmu.eventtracker.geoserver;

import com.caucho.hessian.server.HessianServlet;

public class GeoServiceImpl extends HessianServlet implements GeoService {

	@Override
	public String hello() {
		return "Hello World";
	}

}
