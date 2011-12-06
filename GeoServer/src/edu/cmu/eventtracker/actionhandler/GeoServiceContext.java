package edu.cmu.eventtracker.actionhandler;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;

import edu.cmu.eventtracker.action.Action;
import edu.cmu.eventtracker.action.BatchAction;
import edu.cmu.eventtracker.action.ReplicatableAction;
import edu.cmu.eventtracker.action.ReplicationAction;
import edu.cmu.eventtracker.action.Synchronous;
import edu.cmu.eventtracker.action.UserShardAction;
import edu.cmu.eventtracker.geoserver.GeoServiceImpl;

public class GeoServiceContext implements ActionContext {

	private final ArrayList<ReplicatableAction<?>> actionLog = new ArrayList<ReplicatableAction<?>>();
	private final ArrayList<UserShardAction> userShardActionLog = new ArrayList<UserShardAction>();
	private final GeoServiceImpl service;
	private final boolean replicating;

	public GeoServiceContext(GeoServiceImpl service, boolean replicating) {
		this.service = service;
		this.replicating = replicating;
	}

	public <A extends Action<R>, R> R execute(A action) {
		if (action == null) {
			throw new NullPointerException("Action cannot be null");
		}
		@SuppressWarnings("unchecked")
		ActionHandler<A, R> handler = (ActionHandler<A, R>) service
				.getActionHandlerMap().get(action.getClass());
		if (handler == null) {
			throw new NullPointerException("Handler for "
					+ action.getClass().getSimpleName() + " was not found");
		}
		int lastSize = -1;
		if (service.isMaster() || service.isSlaveFailover()) {
			if (action instanceof Synchronous) {
				lastSize = actionLog.size();
			}
			if (action instanceof ReplicatableAction<?>) {
				actionLog.add((ReplicatableAction<?>) action);
			}
		}
		R response = handler.performAction(action, this);
		if (service.isMaster() && action instanceof Synchronous
				&& lastSize < actionLog.size()) {
			LinkedList<ReplicatableAction<?>> replicatedList = new LinkedList<ReplicatableAction<?>>();
			for (ListIterator<ReplicatableAction<?>> iterator = actionLog
					.listIterator(lastSize); iterator.hasNext();) {
				replicatedList.add(iterator.next());
				iterator.remove();
			}
			if (!replicating) {
				service.getOtherGeoService().execute(
						new ReplicationAction(new BatchAction(replicatedList),
								service.getUrl()));
			}
		}
		return response;
	}

	public void addUserShardAction(UserShardAction action) {
		getUserShardActionLog().add(action);
	}

	public ArrayList<ReplicatableAction<?>> getActionLog() {
		return actionLog;
	}

	public Connection getUsersConnection() {
		return service.getUsersConnection();
	}

	public Connection getLocationsConnection() {
		return service.getLocationsConnection();
	}

	public GeoServiceImpl getService() {
		return service;
	}

	public ArrayList<UserShardAction> getUserShardActionLog() {
		return userShardActionLog;
	}

}
