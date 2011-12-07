package edu.cmu.eventtracker.actionhandler;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import com.effectiveJava.GeoLocationService;
import com.effectiveJava.Point;

import edu.cmu.eventtracker.action.GetCloseByEvents;
import edu.cmu.eventtracker.action.InsertLocationAction;
import edu.cmu.eventtracker.action.LocationHeartbeatAction;
import edu.cmu.eventtracker.dto.Event;
import edu.cmu.eventtracker.dto.Location;
import edu.cmu.eventtracker.dto.LocationHeartbeatResponse;

public class LocationHeartbeatHandler
		implements
			ActionHandler<LocationHeartbeatAction, LocationHeartbeatResponse> {

	public static final int MIN_COUNT = 4;
	public static final int MAX_PERIOD = 60; // minutes
	public static final double RADIUS = 0.5; // km

	@Override
	public LocationHeartbeatResponse performAction(
			LocationHeartbeatAction action, ActionContext context) {
		GeoServiceContext geoContext = (GeoServiceContext) context;
		LocationHeartbeatResponse response = new LocationHeartbeatResponse();
		Location location = action.getLocation();
		location.setId(UUID.randomUUID().toString());
		location.setTimestamp(new Date());
		context.execute(new InsertLocationAction(location));

		HashMap<String, Event> closeByEvents = geoContext
				.execute(new GetCloseByEvents(action.getLocation().getLat(),
						action.getLocation().getLng()));
		if (location.getEventId() != null) {
			Event event = closeByEvents.get(location.getEventId());
			if (event == null) {
				throw new NullPointerException(
						"Event with the given id was not found");
			}
			Point[] extremes = GeoLocationService.getExtremePointsFrom(
					new Point(event.getLocation().getLat(), event.getLocation()
							.getLng()), RADIUS);
			if (!(extremes[0].getLatitude() <= location.getLat()
					&& extremes[0].getLongitude() <= location.getLng()
					&& location.getLat() <= extremes[1].getLatitude() && location
					.getLng() <= extremes[1].getLongitude())) {
				throw new IllegalStateException(
						"You are too far away from the original event");
			}
		}
		response.setEvents(new ArrayList<Event>(closeByEvents.values()));
		response.setCanCreateEvent(canCreateNewEvents(closeByEvents));
		return response;
	}

	public static boolean canCreateNewEvents(
			HashMap<String, Event> closeByEvents) {
		Event usersWithoutEvent = closeByEvents.get(null);
		if (usersWithoutEvent != null
				&& usersWithoutEvent.getParticipantCount() >= MIN_COUNT) {
			return true;
		} else {
			return false;
		}
	}

}
