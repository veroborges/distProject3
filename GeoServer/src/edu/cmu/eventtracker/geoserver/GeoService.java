package edu.cmu.eventtracker.geoserver;

import java.util.List;

public interface GeoService {

    public List<Location> getUserLocations(String username);

	public List<String> getUserEvents(String username);
	public boolean addUser(String username, String name, String pass);
}
