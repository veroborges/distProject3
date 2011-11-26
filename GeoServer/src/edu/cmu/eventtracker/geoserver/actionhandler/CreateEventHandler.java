package edu.cmu.eventtracker.geoserver.actionhandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import edu.cmu.eventtracker.geoserver.action.CreateEventAction;
import edu.cmu.eventtracker.geoserver.dto.Event;

public class CreateEventHandler
		implements
			ActionHandler<CreateEventAction, Event> {

	@Override
	public Event performAction(CreateEventAction action, ActionContext context) {
		GeoServiceContext geoContext = (GeoServiceContext) context;
		try {
			if (!PingHandler.canCreateNewEvents(PingHandler.closeByEvents(
					action.getLat(), action.getLng(), geoContext))) {
				throw new IllegalStateException("Can't create new events yet");
			}
			long locationId = PingHandler.insertLocation(action.getUsername(),
					action.getLat(), action.getLng(), null, geoContext);
			PreparedStatement statement = geoContext
					.getLocationsConnection()
					.prepareStatement(
							"Insert into event(location_id, name) values(?, ?)",
							Statement.RETURN_GENERATED_KEYS);
			statement.setLong(1, locationId);
			statement.setString(2, action.getUsername());
			statement.execute();
			ResultSet res = statement.getGeneratedKeys();
			long eventId = 0;
			while (res.next()) {
				eventId = res.getLong(1);
			}
			statement = geoContext.getLocationsConnection().prepareStatement(
					"update location set event_id = ? where id=?");
			statement.setLong(1, eventId);
			statement.setLong(2, locationId);
			statement.execute();
			return PingHandler.getEvent(eventId, geoContext);
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
	}
}
