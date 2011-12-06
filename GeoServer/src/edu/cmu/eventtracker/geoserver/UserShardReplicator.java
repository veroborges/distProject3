package edu.cmu.eventtracker.geoserver;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingDeque;

import com.caucho.hessian.client.HessianConnectionException;
import com.caucho.hessian.client.HessianRuntimeException;

import edu.cmu.eventtracker.action.ReplicationAction;
import edu.cmu.eventtracker.action.UserShardAction;
import edu.cmu.eventtracker.serverlocator.ServerLocatorCache;
import edu.cmu.eventtracker.serverlocator.ServerLocatorService;

public class UserShardReplicator extends Thread {

	private LinkedBlockingDeque<UserShardAction> actionQueue = new LinkedBlockingDeque<UserShardAction>(
			10);
	private ServerLocatorCache serverLocatorCache;
	private String sourceUrl;

	public UserShardReplicator(ArrayList<ServerLocatorService> locatorServices,
			String sourceUrl) throws MalformedURLException {
		super("UserShardReplicator");
		serverLocatorCache = new ServerLocatorCache(locatorServices);
		this.sourceUrl = sourceUrl;
	}

	@Override
	public void run() {
		UserShardAction action = null;
		try {
			while ((action = actionQueue.take()) != null) {
				GeoService userShardServer = serverLocatorCache
						.getUserShardServer(action.getUsername());
				boolean replicated = false;
				while (!replicated) {
					try {
						userShardServer.execute(new ReplicationAction(action
								.getAction(), sourceUrl));
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
	public void replicateActions(ArrayList<UserShardAction> actions)
			throws InterruptedException {
		for (UserShardAction action : actions) {
			actionQueue.put(action);
		}
	}
}
