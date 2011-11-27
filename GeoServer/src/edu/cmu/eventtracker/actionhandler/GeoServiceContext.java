package edu.cmu.eventtracker.actionhandler;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;

import com.caucho.hessian.client.HessianProxyFactory;

import edu.cmu.eventtracker.action.Action;
import edu.cmu.eventtracker.action.AddUserAction;
import edu.cmu.eventtracker.action.ClearLocationsDBAction;
import edu.cmu.eventtracker.action.ClearUsersDBAction;
import edu.cmu.eventtracker.action.CreateEventAction;
import edu.cmu.eventtracker.action.GetUserAction;
import edu.cmu.eventtracker.action.GetUserEvents;
import edu.cmu.eventtracker.action.GetUserLocations;
import edu.cmu.eventtracker.action.PingAction;
import edu.cmu.eventtracker.action.ReplicatableAction;
import edu.cmu.eventtracker.dto.ShardResponse;
import edu.cmu.eventtracker.geoserver.GeoServer;
import edu.cmu.eventtracker.geoserver.GeoService;
import edu.cmu.eventtracker.geoserver.GeoServiceImpl;
import edu.cmu.eventtracker.geoserver.Replicator;
import edu.cmu.eventtracker.serverlocator.ServerLocatorService;

public class GeoServiceContext implements ActionContext {
	private static final String protocol = "jdbc:derby:";

	private HashMap<Class<? extends Action<?>>, ActionHandler<?, ?>> actionHandlerMap;
	private final Connection usersConnection;
	private final Connection locationsConnection;
	private boolean master;
	private ServerLocatorService locatorService;
	private GeoService otherGeoService;
	private Replicator replicator;

	public GeoServiceContext(GeoServiceImpl service) {
		int port = (Integer) service.getServletContext().getAttribute("PORT");
		master = (Boolean) service.getServletContext().getAttribute("MASTER");
		String serverLocatorURL = (String) service.getServletContext()
				.getAttribute("SERVER_LOCATOR");
		try {
			usersConnection = DriverManager.getConnection(protocol + "usersDB"
					+ port + ";create=true", null);
			locationsConnection = DriverManager.getConnection(protocol
					+ "locationsDB" + port + ";create=true", null);
			getUsersConnection().setAutoCommit(false);
			getLocationsConnection().setAutoCommit(false);
			String url = GeoServer.getURL(InetAddress.getLocalHost()
					.getHostName(), port);
			HessianProxyFactory factory = new HessianProxyFactory();
			factory.setConnectTimeout(500);
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
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		} catch (UnknownHostException e) {
			throw new IllegalStateException(e);
		} catch (MalformedURLException e) {
			throw new IllegalStateException(e);
		}
		actionHandlerMap = new HashMap<Class<? extends Action<?>>, ActionHandler<?, ?>>();
		actionHandlerMap.put(AddUserAction.class, new AddUserHandler());
		actionHandlerMap.put(ClearLocationsDBAction.class,
				new ClearLocationsDBHandler());
		actionHandlerMap.put(ClearUsersDBAction.class,
				new ClearUsersDBHandler());
		actionHandlerMap.put(GetUserAction.class, new GetUserHandler());
		actionHandlerMap.put(GetUserEvents.class, new GetUserEventsHandler());
		actionHandlerMap.put(GetUserLocations.class,
				new GetUserLocationsHandler());
		actionHandlerMap.put(PingAction.class, new PingHandler());
		actionHandlerMap.put(CreateEventAction.class, new CreateEventHandler());
	}
	public <A extends Action<R>, R> R execute(A action) {
		if (action == null) {
			throw new NullPointerException("Action cannot be null");
		}
		@SuppressWarnings("unchecked")
		ActionHandler<A, R> handler = (ActionHandler<A, R>) actionHandlerMap
				.get(action.getClass());
		R response = handler.performAction(action, this);
		if (master && action instanceof ReplicatableAction) {
			try {
				replicator.replicateAction((ReplicatableAction<?>) action);
			} catch (InterruptedException e) {
				System.out.println("problem replicating action " + action);
				e.printStackTrace();
			}
		}
		return response;
	}

	public Connection getUsersConnection() {
		return usersConnection;
	}

	public Connection getLocationsConnection() {
		return locationsConnection;
	}
}
