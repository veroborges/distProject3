package edu.cmu.eventtracker.geoserver;

import edu.cmu.eventtracker.action.Action;

public interface GeoService {
	public static final long TIMEOUT = 5000;
	public <A extends Action<R>, R> R execute(A action);
}
