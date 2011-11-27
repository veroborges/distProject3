package edu.cmu.eventtracker.geoserver;

import java.net.MalformedURLException;
import java.util.concurrent.LinkedBlockingDeque;

import edu.cmu.eventtracker.action.Action;
import edu.cmu.eventtracker.action.ReplicatableAction;

public class Replicator extends Thread {

	private GeoService slave;
	private LinkedBlockingDeque<Action<?>> actionQueue = new LinkedBlockingDeque<Action<?>>(
			10);

	public Replicator(GeoService slave) throws MalformedURLException {
		super("Replicator");
		this.slave = slave;
	}

	@Override
	public void run() {
		Action<?> action = null;
		try {
			while ((action = actionQueue.take()) != null) {
				slave.execute(action);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void replicateAction(ReplicatableAction<?> action)
			throws InterruptedException {
		actionQueue.put(action);
	}

}
