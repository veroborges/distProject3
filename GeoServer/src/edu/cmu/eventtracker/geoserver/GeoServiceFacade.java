package edu.cmu.eventtracker.geoserver;

import java.net.MalformedURLException;

import com.caucho.hessian.client.HessianProxyFactory;

import edu.cmu.eventtracker.action.Action;
import edu.cmu.eventtracker.dto.ShardResponse;

public class GeoServiceFacade implements GeoService {

	private GeoService masterServer;
	private GeoService slaveServer;
	private HessianProxyFactory factory;
	private boolean master;

	public GeoServiceFacade(ShardResponse shards) throws MalformedURLException {
		factory = new HessianProxyFactory();
		factory.setConnectTimeout(GeoService.TIMEOUT);
		factory.setReadTimeout(GeoService.TIMEOUT);
		masterServer = getGeoServiceConnection(shards.getMaster());
		slaveServer = getGeoServiceConnection(shards.getSlave());
	}

	@Override
	public <A extends Action<R>, R> R execute(A action) {
		if (isMaster()) {
			return masterServer.execute(action);
		} else {
			return slaveServer.execute(action);
		}
	}

	private GeoService getGeoServiceConnection(String url)
			throws MalformedURLException {
		return (GeoService) factory.create(GeoService.class, url);
	}

	public boolean isMaster() {
		return master;
	}

	public void setMaster(boolean master) {
		this.master = master;
	}

}
