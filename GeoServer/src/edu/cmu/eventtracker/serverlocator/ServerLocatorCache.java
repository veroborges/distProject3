package edu.cmu.eventtracker.serverlocator;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import edu.cmu.eventtracker.dto.ShardResponse;
import edu.cmu.eventtracker.geoserver.GeoService;
import edu.cmu.eventtracker.geoserver.GeoServiceFacade;

public class ServerLocatorCache implements ServerLocatorService {

	private ArrayList<ServerLocatorService> services = new ArrayList<ServerLocatorService>();
	private Cache<String, GeoService> userShardCache = new Cache<String, GeoService>();
	private Cache<ShardResponse, GeoService> locationShardCache = new Cache<ShardResponse, GeoService>();
	private int pos = 0;

	public ServerLocatorCache(ArrayList<ServerLocatorService> services) {
		this.services = services;
	}

	public GeoService getUserShardServer(String username) {
		GeoService geoService = userShardCache.get(username);
		if (geoService == null) {
			RuntimeException lastException = null;
			for (int i = 0; i < services.size(); i++) {
				try {
					GeoServiceFacade facade = new GeoServiceFacade(services
							.get(i).getUserShard(username));
					Calendar calendar = Calendar.getInstance();
					calendar.add(Calendar.MINUTE, 20);
					userShardCache.put(username, facade, calendar);
					return facade;
				} catch (RuntimeException e) {
					lastException = e;
				} catch (MalformedURLException e) {
					lastException = new IllegalStateException(e);
				}
				pos = (pos + 1) % services.size();
			}
			throw lastException;
		} else {
			return geoService;
		}
	}

	public GeoService getLocationShardServer(double lat, double lng) {
		ShardResponse response = getLocationShard(lat, lng);
		GeoService geoService = locationShardCache.get(response);
		if (geoService == null) {
			GeoServiceFacade facade;
			try {
				facade = new GeoServiceFacade(response);
				Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.MINUTE, 20);
				locationShardCache.put(response, facade, calendar);
				return facade;
			} catch (MalformedURLException e) {
				throw new IllegalStateException(e);
			}
		} else {
			return geoService;
		}
	}

	private class Cache<K, V> {
		private HashMap<K, CacheEntry<V>> cache = new HashMap<K, CacheEntry<V>>();

		public V get(K key) {
			CacheEntry<V> entry = cache.get(key);
			if (entry == null || entry.date.before(Calendar.getInstance())) {
				cache.remove(key);
				return null;
			}
			return entry.value;
		}

		public void put(K key, V value, Calendar expireTime) {
			cache.put(key, new CacheEntry<V>(value, expireTime));
		}

	}

	private class CacheEntry<V> {
		V value;
		Calendar date;
		public CacheEntry(V value, Calendar date) {
			this.value = value;
			this.date = date;
		}
	}

	@Override
	public void addUserShard(int nodeid, String master, String slave) {
		for (ServerLocatorService service : services) {
			service.addUserShard(nodeid, master, slave);
		}
	}

	@Override
	public void addLocationShard(double latmin, double lngmin, double latmax,
			double lngmax, String master, String slave, String name) {
		for (ServerLocatorService service : services) {
			service.addLocationShard(latmin, lngmin, latmax, lngmax, master,
					slave, name);
		}
	}

	@Override
	public void clearTables() {
		for (ServerLocatorService service : services) {
			service.clearTables();
		}
	}

	@Override
	public ShardResponse findLocationShard(String url) {
		RuntimeException lastException = null;
		for (int i = 0; i < services.size(); i++) {
			try {
				return services.get(i).findLocationShard(url);
			} catch (RuntimeException e) {
				lastException = e;
			}
			pos = (pos + 1) % services.size();
		}
		throw lastException;
	}

	@Override
	public ShardResponse getUserShard(String username) {
		RuntimeException lastException = null;
		for (int i = 0; i < services.size(); i++) {
			try {
				return services.get(i).getUserShard(username);
			} catch (RuntimeException e) {
				lastException = e;
			}
			pos = (pos + 1) % services.size();
		}
		throw lastException;
	}

	@Override
	public ShardResponse getLocationShard(double lat, double lng) {
		RuntimeException lastException = null;
		for (int i = 0; i < services.size(); i++) {
			try {
				return services.get(i).getLocationShard(lat, lng);
			} catch (RuntimeException e) {
				lastException = e;
			}
			pos = (pos + 1) % services.size();
		}
		throw lastException;
	}

}
