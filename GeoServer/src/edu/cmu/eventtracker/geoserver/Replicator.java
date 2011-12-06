package edu.cmu.eventtracker.geoserver;

import java.net.MalformedURLException;
import java.util.concurrent.LinkedBlockingDeque;

import com.caucho.hessian.client.HessianConnectionException;
import com.caucho.hessian.client.HessianRuntimeException;

import edu.cmu.eventtracker.action.Action;
import edu.cmu.eventtracker.action.ReplicationAction;

public class Replicator extends Thread {

	private GeoService otherGeoServer;
	private LinkedBlockingDeque<Action<?>> actionQueue = new LinkedBlockingDeque<Action<?>>(
			10);
	private String sourceUrl;

	public Replicator(GeoService otherGeoServer, String sourceUrl)
			throws MalformedURLException {
		super("Replicator");
		this.otherGeoServer = otherGeoServer;
		this.sourceUrl = sourceUrl;
	}

	@Override
	public void run() {
		Action<?> action = null;
		try {
			while ((action = actionQueue.take()) != null) {
				boolean replicated = false;
				while (!replicated) {
					try {
						otherGeoServer.execute(new ReplicationAction(action,
								sourceUrl));
						replicated = true;
					} catch (HessianRuntimeException e) {
					} catch (HessianConnectionException e) {
					}
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void replicateAction(Action<?> action) throws InterruptedException {
		actionQueue.put(action);
	}

}
