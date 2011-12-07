package edu.cmu.eventtracker.serverlocator;

import java.util.List;

import edu.cmu.eventtracker.dto.ShardResponse;

public interface ServerLocatorService {

	public static final int SERVER_LOCATOR_PORT = 8888;

	public ShardResponse getUserShard(String username);

	public ShardResponse getLocationShard(double lat, double lng);

	public void addUserShard(int nodeid, String master, String slave);

	public void addLocationShard(double latmin, double lngmin, double latmax,
			double lngmax, String master, String slave, String name);

	public void clearTables();

	public ShardResponse findLocationShard(String url);

	public List<ShardResponse> getAllLocationShards();

}
