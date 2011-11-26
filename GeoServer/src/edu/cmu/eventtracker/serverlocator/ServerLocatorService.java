package edu.cmu.eventtracker.serverlocator;

public interface ServerLocatorService {

	public String getUserShard(String username);
	public String getLocationShard(double lat, double lng);
	public void addUserShard(int nodeid, String hostname);
	public void addLocationShard(double latmin, double lngmin, double latmax,
			double lngmax, String hostname, String name);

	public void clearTables();

}
