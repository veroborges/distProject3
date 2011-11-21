package edu.cmu.eventtracker.geoserver;

public interface ServerLocatorService {

	public String getUserShard(String username);
	public String getLocationShard(double lat, double lng);
	
}
