package edu.cmu.eventtracker.geoserver;

import java.util.List;

public interface GeoService {

    public List<Location> getUserLocations(String username);
}
