package edu.cmu.eventtracker.actionhandler;

import java.sql.SQLException;
import java.util.UUID;

import edu.cmu.eventtracker.action.CreateEventAction;
import edu.cmu.eventtracker.action.InsertEventAction;
import edu.cmu.eventtracker.action.PingAction;
import edu.cmu.eventtracker.dto.Event;
import edu.cmu.eventtracker.dto.Location;

public class CreateEventHandler
		implements
			ActionHandler<CreateEventAction, Event> {

	@Override
	public Event performAction(CreateEventAction action, ActionContext context) {
		GeoServiceContext geoContext = (GeoServiceContext) context;
		try {
			if (!PingHandler.canCreateNewEvents(PingHandler.closeByEvents(
					action.getEvent().getLocation().getLat(), action.getEvent()
							.getLocation().getLng(), geoContext))) {
				throw new IllegalStateException("Can't create new events yet");
			}
			Location location = action.getEvent().getLocation();
			action.getEvent().setId(UUID.randomUUID().toString());
			location.setEventId(action.getEvent().getId());
			context.execute(new InsertEventAction(action.getEvent()));
			context.execute(new PingAction(location));

			return PingHandler.getEvent(action.getEvent().getId(), geoContext);
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
	}

}
