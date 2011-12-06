package edu.cmu.eventtracker.actionhandler;

import java.sql.SQLException;
import java.util.UUID;

import edu.cmu.eventtracker.action.CreateEventAction;
import edu.cmu.eventtracker.action.GetEventAction;
import edu.cmu.eventtracker.action.InsertEventAction;
import edu.cmu.eventtracker.action.LocationHeartbeatAction;
import edu.cmu.eventtracker.dto.Event;
import edu.cmu.eventtracker.dto.Location;

public class CreateEventHandler implements
		ActionHandler<CreateEventAction, Event> {

	@Override
	public Event performAction(CreateEventAction action, ActionContext context) {
		GeoServiceContext geoContext = (GeoServiceContext) context;
		try {
			if (!LocationHeartbeatHandler
					.canCreateNewEvents(LocationHeartbeatHandler.closeByEvents(
							action.getEvent().getLocation().getLat(), action
									.getEvent().getLocation().getLng(),
							geoContext))) {
				throw new IllegalStateException("Can't create new events yet");
			}
			Location location = action.getEvent().getLocation();
			action.getEvent().setId(UUID.randomUUID().toString());
			location.setEventId(action.getEvent().getId());
			context.execute(new InsertEventAction(action.getEvent()));
			context.execute(new LocationHeartbeatAction(location));

			return geoContext.execute(new GetEventAction(action.getEvent()
					.getId()));
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
	}
}
