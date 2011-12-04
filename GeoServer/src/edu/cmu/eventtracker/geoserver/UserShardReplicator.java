package edu.cmu.eventtracker.geoserver;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingDeque;

import edu.cmu.eventtracker.action.ReplicationAction;
import edu.cmu.eventtracker.action.UserShardAction;
import edu.cmu.eventtracker.serverlocator.ServerLocatorCache;
import edu.cmu.eventtracker.serverlocator.ServerLocatorService;

public class UserShardReplicator extends Thread {

	private LinkedBlockingDeque<UserShardAction> actionQueue = new LinkedBlockingDeque<UserShardAction>(
			10);
	private ServerLocatorCache serverLocatorCache;

	public UserShardReplicator(boolean master,
			ArrayList<ServerLocatorService> locatorServices)
			throws MalformedURLException {
		super("UserShardReplicator");
		serverLocatorCache = new ServerLocatorCache(locatorServices, master);
	}

	@Override
	public void run() {
		UserShardAction action = null;
		try {
			while ((action = actionQueue.take()) != null) {
				serverLocatorCache.getUserShardServer(action.getUsername())
						.execute(new ReplicationAction(action.getAction()));
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
