package edu.cmu.eventtracker.servlet.EventTracker;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.caucho.hessian.client.HessianProxyFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import edu.cmu.eventtracker.action.GetAllEventsAction;
import edu.cmu.eventtracker.dto.Event;
import edu.cmu.eventtracker.dto.ShardResponse;
import edu.cmu.eventtracker.geoserver.GeoService;
import edu.cmu.eventtracker.geoserver.GeoServiceFacade;
import edu.cmu.eventtracker.serverlocator.ServerLocatorCache;
import edu.cmu.eventtracker.serverlocator.ServerLocatorService;

/**
 * Servlet implementation class AllEventsServlet
 */
public class AllEventsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private HessianProxyFactory factory;
	private InetAddress addr;
	private Gson gson;
	private String json;
	private ServerLocatorCache locatorService;

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
	 * @see HttpServlet#HttpServlet()
	 */
	public AllEventsServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		ArrayList<ShardResponse> shards = (ArrayList<ShardResponse>) locatorService
				.getAllLocationShards();
		List<Event> events = new ArrayList<Event>();
		GeoService geoService;

		Double lat1 = Double.parseDouble(request.getParameter("lat1"));
		Double lng1 = Double.parseDouble(request.getParameter("lng1"));
		Double lat2 = Double.parseDouble(request.getParameter("lat2"));
		Double lng2 = Double.parseDouble(request.getParameter("lng2"));

		for (ShardResponse shard : shards) {
			geoService = new GeoServiceFacade(shard);
			events.addAll(geoService.execute(new GetAllEventsAction(lat1, lng1,
					lat2, lng2)));
		}

		gson = new GsonBuilder().create();
		json = gson.toJson(events);
		System.out.println(json);

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
	}

	private ServerLocatorCache getServiceLocator()
			throws MalformedURLException {

		ArrayList<ServerLocatorService> services = new ArrayList<ServerLocatorService>();
		for (int i = 0; i < 2; i++) {
			String locatorURL = getServiceLocatorURL(addr.getHostName(),
					ServerLocatorService.START_PORT + i);
			ServerLocatorService service = (ServerLocatorService) factory.create(
					ServerLocatorService.class, locatorURL);
			services.add(service);
		}
		return new ServerLocatorCache(services);
	}

	public static String getServiceLocatorURL(String hostname, int port) {
		return "http://" + hostname + ":" + port + "/"
				+ ServerLocatorService.class.getSimpleName();
	}

}
