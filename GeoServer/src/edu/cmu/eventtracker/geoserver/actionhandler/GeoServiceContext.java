package edu.cmu.eventtracker.geoserver.actionhandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;

import edu.cmu.eventtracker.geoserver.GeoServiceImpl;
import edu.cmu.eventtracker.geoserver.action.Action;
import edu.cmu.eventtracker.geoserver.action.AddUserAction;
import edu.cmu.eventtracker.geoserver.action.ClearLocationsDBAction;
import edu.cmu.eventtracker.geoserver.action.ClearUsersDBAction;
import edu.cmu.eventtracker.geoserver.action.CreateEventAction;
import edu.cmu.eventtracker.geoserver.action.GetUserAction;
import edu.cmu.eventtracker.geoserver.action.GetUserEvents;
import edu.cmu.eventtracker.geoserver.action.GetUserLocations;
import edu.cmu.eventtracker.geoserver.action.PingAction;

public class GeoServiceContext implements ActionContext {
	private static final String protocol = "jdbc:derby:";

	private HashMap<Class<? extends Action<?>>, ActionHandler<?, ?>> actionHandlerMap;
	private final Connection usersConnection;
	private final Connection locationsConnection;

	public GeoServiceContext(GeoServiceImpl service) {
		int port = (Integer) service.getServletContext().getAttribute("PORT");
		try {
			usersConnection = DriverManager.getConnection(protocol + "usersDB"
					+ port + ";create=true", null);
			locationsConnection = DriverManager.getConnection(protocol
					+ "locationsDB" + port + ";create=true", null);
			getUsersConnection().setAutoCommit(false);
			getLocationsConnection().setAutoCommit(false);
		} catch (SQLException e) {
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
		return handler.performAction(action, this);
	}

	public Connection getUsersConnection() {
		return usersConnection;
	}

	public Connection getLocationsConnection() {
		return locationsConnection;
	}
}
