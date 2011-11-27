package edu.cmu.eventtracker.geoserver;

import java.net.MalformedURLException;

import com.caucho.hessian.client.HessianProxyFactory;

import edu.cmu.eventtracker.action.Action;
import edu.cmu.eventtracker.dto.ShardResponse;

public class GeoServiceFacade implements GeoService {

	private GeoService master;
	private GeoService slave;
	private HessianProxyFactory factory;

	public GeoServiceFacade(ShardResponse shards) throws MalformedURLException {
		factory = new HessianProxyFactory();
		factory.setConnectTimeout(500);
		master = getGeoServiceConnection(shards.getMaster());
		slave = getGeoServiceConnection(shards.getSlave());
	}

	@Override
	public <A extends Action<R>, R> R execute(A action) {
		return master.execute(action);
	}

	private GeoService getGeoServiceConnection(String url)
			throws MalformedURLException {
		return (GeoService) factory.create(GeoService.class, url);
	}

}
