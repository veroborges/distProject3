package edu.cmu.eventtracker.geoserver;


public interface ServerLocatorService {

	public String getUserShard(String username);
	public String getLocationShard(double lat, double lng);
	public void addUserShard(int nodeid, String hostname);
	public void addLocationShard(int latmin, int lngmin, int latmax,
			int lngmax, String hostname);

	public void clearTables();

}
