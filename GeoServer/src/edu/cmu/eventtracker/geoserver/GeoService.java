package edu.cmu.eventtracker.geoserver;

import edu.cmu.eventtracker.action.Action;

public interface GeoService {

	public <A extends Action<R>, R> R execute(A action);
}
