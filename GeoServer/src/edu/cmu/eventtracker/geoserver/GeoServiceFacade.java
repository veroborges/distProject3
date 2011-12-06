package edu.cmu.eventtracker.geoserver;

import java.net.MalformedURLException;

import com.caucho.hessian.client.HessianConnectionException;
import com.caucho.hessian.client.HessianProxyFactory;
import com.caucho.hessian.client.HessianRuntimeException;

import edu.cmu.eventtracker.action.Action;
import edu.cmu.eventtracker.dto.ShardResponse;

public class GeoServiceFacade implements GeoService {

	private GeoService masterServer;
	private GeoService slaveServer;
	private HessianProxyFactory factory;
	private boolean switchToSlave = false;

	public GeoServiceFacade(ShardResponse shards) throws MalformedURLException {
		factory = new HessianProxyFactory();
		factory.setConnectTimeout(GeoService.TIMEOUT);
		factory.setReadTimeout(GeoService.TIMEOUT);
		masterServer = getGeoServiceConnection(shards.getMaster());
		slaveServer = getGeoServiceConnection(shards.getSlave());
	}

	@Override
	public <A extends Action<R>, R> R execute(A action) {
		if (!switchToSlave) {
			try {
				return masterServer.execute(action);
			} catch (HessianRuntimeException e) {
				R response = slaveServer.execute(action);
				switchToSlave = true;
				return response;
			} catch (HessianConnectionException e) {
				R response = slaveServer.execute(action);
				switchToSlave = true;
				return response;
			}
		} else {
			try {
				return slaveServer.execute(action);
			} catch (ExecuteOnMasterException e) {
				R response = masterServer.execute(action);
				switchToSlave = false;
				return response;
			}
		}
	}
	private GeoService getGeoServiceConnection(String url)
			throws MalformedURLException {
		return (GeoService) factory.create(GeoService.class, url);
	}

}
