package edu.cmu.eventtracker.geoserver;

import com.caucho.hessian.server.HessianServlet;

public class ServerLocatorServiceImpl extends HessianServlet
		implements
			ServerLocatorService {

	@Override
	public String getUserShard(String username) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLocationShard(double lat, double lng) {
		// TODO Auto-generated method stub
		return null;
	}

}
