package edu.cmu.eventtracker.servlet.EventTracker;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.caucho.hessian.client.HessianProxyFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import edu.cmu.eventtracker.action.AddUserAction;
import edu.cmu.eventtracker.action.CreateEventAction;
import edu.cmu.eventtracker.action.GetAllEventsAction;
import edu.cmu.eventtracker.action.LocationHeartbeatAction;
import edu.cmu.eventtracker.dto.Event;
import edu.cmu.eventtracker.dto.Location;
import edu.cmu.eventtracker.dto.LocationHeartbeatResponse;
import edu.cmu.eventtracker.dto.ShardResponse;
import edu.cmu.eventtracker.geoserver.GeoService;
import edu.cmu.eventtracker.geoserver.GeoServiceFacade;
import edu.cmu.eventtracker.serverlocator.ServerLocatorCache;
import edu.cmu.eventtracker.serverlocator.ServerLocatorService;
/**
 * Servlet implementation class EventTrackerServlet
 */
public class EventTrackerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private HessianProxyFactory factory;
	private InetAddress addr;
	private Gson gson;
	private String json;
	ServerLocatorCache locatorService;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public EventTrackerServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init() throws ServletException {
		super.init();
		try {
			factory = new HessianProxyFactory();
			factory.setConnectTimeout(GeoService.TIMEOUT);
			factory.setReadTimeout(GeoService.TIMEOUT);
			addr = InetAddress.getLocalHost();
			locatorService = getServiceLocator();

		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String username = request.getParameter("pUser");
		if (username != null) {
			String lat = request.getParameter("pLat");
			String lng = request.getParameter("pLng");
			String eventid = request.getParameter("pEventId");

			if (eventid == null || eventid.equals("null")) {
				eventid = null;
			}

			double latD = Double.parseDouble(lat);
			double lngD = Double.parseDouble(lng);

			Location loc = new Location(null, latD, lngD, username, eventid,
					null);

			LocationHeartbeatResponse res = locatorService
					.getLocationShardServer(latD, lngD).execute(
							new LocationHeartbeatAction(loc));

			System.out.println("can create:" + res.canCreateEvent());
			System.out.println("events:" + res.getEvents());
			gson = new GsonBuilder().create();
			String json = gson.toJson(res);

			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(json);
		}

		else {
			ArrayList<ShardResponse> shards = (ArrayList<ShardResponse>) locatorService
					.getAllLocationShards();
			System.out.println("shard size" + shards.size());
			gson = new GsonBuilder().create();
			String json = gson.toJson(shards);

			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(json);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// add user functionality
		if (request.getParameter("mUser") != null) {
			doCreateUser(request);
		} else if (request.getParameter("cUser") != null) {
			doCreateEvent(request);
		}

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);

	}

	private void doCreateEvent(HttpServletRequest request)
			throws ServletException, IOException {
		String username = request.getParameter("cUser");
		String lat = request.getParameter("cLat");
		String lng = request.getParameter("cLng");
		String eventname = request.getParameter("cEventName");

		System.out
				.println("user:" + username + " lat: " + lat + " lon: " + lng);
		double latD = Double.parseDouble(lat);
		double lngD = Double.parseDouble(lng);



		Event res = locatorService.getLocationShardServer(latD, lngD).execute(new CreateEventAction(latD, lngD,
				username, eventname));

		gson = new GsonBuilder().create();
		json = gson.toJson(res);
	}

	private void doCreateUser(HttpServletRequest request)
			throws ServletException, IOException {
		String username = request.getParameter("mUser");
		String name = request.getParameter("mName");
		String password = request.getParameter("mPass");

		GeoService geoService = new GeoServiceFacade(
				locatorService.getUserShard(username));

		geoService.execute(new AddUserAction(username, name, password));

		gson = new GsonBuilder().create();
		json = gson.toJson(username);
	}

	private ServerLocatorCache getServiceLocator() throws MalformedURLException {

		ArrayList<ServerLocatorService> services = new ArrayList<ServerLocatorService>();
		for (int i = 0; i < 2; i++) {
			String locatorURL = getServiceLocatorURL(addr.getHostName(),
					ServerLocatorService.START_PORT + i);
			ServerLocatorService service = (ServerLocatorService) factory
					.create(ServerLocatorService.class, locatorURL);
			services.add(service);
		}
		return new ServerLocatorCache(services);
	}

	public static String getServiceLocatorURL(String hostname, int port) {
		return "http://" + hostname + ":" + port + "/"
				+ ServerLocatorService.class.getSimpleName();
	}

}
