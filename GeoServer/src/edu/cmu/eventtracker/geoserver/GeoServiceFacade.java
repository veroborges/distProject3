package edu.cmu.eventtracker.geoserver;

import java.net.MalformedURLException;

import com.caucho.hessian.client.HessianConnectionException;
import com.caucho.hessian.client.HessianProxyFactory;

import edu.cmu.eventtracker.action.Action;
import edu.cmu.eventtracker.action.ReadOnlyAction;
import edu.cmu.eventtracker.dto.ShardResponse;

public class GeoServiceFacade implements GeoService {

	private GeoService masterServer;
	private GeoService slaveServer;
	private HessianProxyFactory factory;
	private boolean master = true;

	public GeoServiceFacade(ShardResponse shards) throws MalformedURLException {
		factory = new HessianProxyFactory();
		factory.setConnectTimeout(GeoService.TIMEOUT);
		factory.setReadTimeout(GeoService.TIMEOUT);
		masterServer = getGeoServiceConnection(shards.getMaster());
		slaveServer = getGeoServiceConnection(shards.getSlave());
	}

	@Override
	public <A extends Action<R>, R> R execute(A action) {
		if (action instanceof ReadOnlyAction) {
			return slaveServer.execute(action);
		} else if (isMaster()) {
			try {
				return masterServer.execute(action);
			} catch (HessianConnectionException e) {
				return slaveServer.execute(action);
			}
		}
		throw new IllegalStateException("Facade is misconfigured");
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
