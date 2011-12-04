package edu.cmu.eventtracker.geoserver;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletException;

import com.caucho.hessian.client.HessianProxyFactory;
import com.caucho.hessian.server.HessianServlet;

import edu.cmu.eventtracker.action.Action;
import edu.cmu.eventtracker.action.AddUserAction;
import edu.cmu.eventtracker.action.BatchAction;
import edu.cmu.eventtracker.action.ClearLocationsDBAction;
import edu.cmu.eventtracker.action.ClearUsersDBAction;
import edu.cmu.eventtracker.action.CreateEventAction;
import edu.cmu.eventtracker.action.GetUserAction;
import edu.cmu.eventtracker.action.GetUserEvents;
import edu.cmu.eventtracker.action.GetUserLocations;
import edu.cmu.eventtracker.action.InsertEventAction;
import edu.cmu.eventtracker.action.InsertLocationAction;
import edu.cmu.eventtracker.action.PingAction;
import edu.cmu.eventtracker.actionhandler.ActionHandler;
import edu.cmu.eventtracker.actionhandler.AddUserHandler;
import edu.cmu.eventtracker.actionhandler.BatchHandler;
import edu.cmu.eventtracker.actionhandler.ClearLocationsDBHandler;
import edu.cmu.eventtracker.actionhandler.ClearUsersDBHandler;
import edu.cmu.eventtracker.actionhandler.CreateEventHandler;
import edu.cmu.eventtracker.actionhandler.GeoServiceContext;
import edu.cmu.eventtracker.actionhandler.GetUserEventsHandler;
import edu.cmu.eventtracker.actionhandler.GetUserHandler;
import edu.cmu.eventtracker.actionhandler.GetUserLocationsHandler;
import edu.cmu.eventtracker.actionhandler.InsertEventHandler;
import edu.cmu.eventtracker.actionhandler.InsertLocationHandler;
import edu.cmu.eventtracker.actionhandler.PingHandler;
import edu.cmu.eventtracker.dto.ShardResponse;
import edu.cmu.eventtracker.serverlocator.ServerLocatorService;

public class GeoServiceImpl extends HessianServlet implements GeoService {

	private static final String protocol = "jdbc:derby:";
	private final HashMap<Class<? extends Action<?>>, ActionHandler<?, ?>> actionHandlerMap = new HashMap<Class<? extends Action<?>>, ActionHandler<?, ?>>();
	private Connection usersConnection;
	private Connection locationsConnection;
	private boolean master;
	private ServerLocatorService locatorService;
	private GeoService otherGeoService;
	private Replicator replicator;
	private UserShardReplicator userShardReplicator;
	private String url;

	@Override
	public void init() throws ServletException {
		super.init();
		int port = (Integer) getServletContext().getAttribute("PORT");
		master = (Boolean) getServletContext().getAttribute("MASTER");
		String serverLocatorURL = (String) getServletContext().getAttribute(
				"SERVER_LOCATOR");
		try {
			usersConnection = DriverManager.getConnection(protocol + "usersDB"
					+ port, null);
			locationsConnection = DriverManager.getConnection(protocol
					+ "locationsDB" + port, null);
			usersConnection.setAutoCommit(false);
			locationsConnection.setAutoCommit(false);
			url = GeoServer.getURL(InetAddress.getLocalHost().getHostName(),
					port);
			HessianProxyFactory factory = new HessianProxyFactory();
			factory.setConnectTimeout(TIMEOUT);
			factory.setReadTimeout(TIMEOUT);
			locatorService = (ServerLocatorService) factory.create(
					ServerLocatorService.class, serverLocatorURL);
			ShardResponse locationShard = locatorService.findLocationShard(url);
			if (master) {
				otherGeoService = (GeoService) factory.create(GeoService.class,
						locationShard.getSlave());
				replicator = new Replicator(otherGeoService);
				replicator.start();
			} else {
				otherGeoService = (GeoService) factory.create(GeoService.class,
						locationShard.getMaster());
			}
			ArrayList<ServerLocatorService> services = new ArrayList<ServerLocatorService>();
			services.add(locatorService);
			userShardReplicator = new UserShardReplicator(master, services);
			getUserShardReplicator().start();
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		} catch (UnknownHostException e) {
			throw new IllegalStateException(e);
		} catch (MalformedURLException e) {
			throw new IllegalStateException(e);
		}
		getActionHandlerMap().put(AddUserAction.class, new AddUserHandler());
		getActionHandlerMap().put(ClearLocationsDBAction.class,
				new ClearLocationsDBHandler());
		getActionHandlerMap().put(ClearUsersDBAction.class,
				new ClearUsersDBHandler());
		getActionHandlerMap().put(GetUserAction.class, new GetUserHandler());
		getActionHandlerMap().put(GetUserEvents.class,
				new GetUserEventsHandler());
		getActionHandlerMap().put(GetUserLocations.class,
				new GetUserLocationsHandler());
		getActionHandlerMap().put(PingAction.class, new PingHandler());
		getActionHandlerMap().put(CreateEventAction.class,
				new CreateEventHandler());
		getActionHandlerMap().put(InsertLocationAction.class,
				new InsertLocationHandler());
		getActionHandlerMap().put(InsertEventAction.class,
				new InsertEventHandler());
		getActionHandlerMap().put(BatchAction.class, new BatchHandler());
	}

	@Override
	public <A extends Action<R>, R> R execute(A action) {
		boolean commit = true;
		GeoServiceContext context = new GeoServiceContext(this);
		try {
			return context.execute(action);
		} catch (RuntimeException ex) {
			commit = false;
			throw ex;
		} finally {
			if (commit) {
				try {
					usersConnection.commit();
					locationsConnection.commit();
					if (!context.getActionLog().isEmpty()) {
						try {
							replicator.replicateAction(new BatchAction(context
									.getActionLog()));
							userShardReplicator.replicateActions(context
									.getUserShardActionLog());
						} catch (InterruptedException e) {
							throw new IllegalStateException(e);
						}
					}
				} catch (SQLException e) {
					throw new IllegalStateException(e);
				}
			} else {
				try {
					usersConnection.rollback();
					locationsConnection.rollback();
				} catch (SQLException e) {
					throw new IllegalStateException(e);
				}
			}
		}
	}
	public HashMap<Class<? extends Action<?>>, ActionHandler<?, ?>> getActionHandlerMap() {
		return actionHandlerMap;
	}

	public boolean isMaster() {
		return master;
	}

	public GeoService getOtherGeoService() {
		return otherGeoService;
	}

	public Connection getUsersConnection() {
		return usersConnection;
	}

	public Connection getLocationsConnection() {
		return locationsConnection;
	}

	public UserShardReplicator getUserShardReplicator() {
		return userShardReplicator;
	}

	public String getUrl() {
		return url;
	}

}
