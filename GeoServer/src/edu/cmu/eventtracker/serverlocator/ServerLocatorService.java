package edu.cmu.eventtracker.serverlocator;

import edu.cmu.eventtracker.dto.ShardResponse;

public interface ServerLocatorService {

	public String getUserShard(String username);
	public ShardResponse getLocationShard(double lat, double lng);
	public void addUserShard(int nodeid, String hostname);
	public void addLocationShard(double latmin, double lngmin, double latmax,
			double lngmax, String master, String slave, String name);

	public void clearTables();

}
