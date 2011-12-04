package edu.cmu.eventtracker.servlet.EventTracker;


import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


import com.caucho.hessian.client.HessianProxyFactory;

import edu.cmu.eventtracker.action.AddUserAction;
import edu.cmu.eventtracker.action.GetUserAction;
import edu.cmu.eventtracker.action.PingAction;
import edu.cmu.eventtracker.dto.Event;
//import edu.cmu.eventtracker.dto.PingResponse;
import edu.cmu.eventtracker.geoserver.GeoService;
import edu.cmu.eventtracker.geoserver.GeoServiceFacade;
import edu.cmu.eventtracker.serverlocator.ServerLocator;
import edu.cmu.eventtracker.serverlocator.ServerLocatorService;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
/**
 * Servlet implementation class EventTrackerServlet
 */
public class EventTrackerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private HessianProxyFactory factory;
	private InetAddress addr;
	private Gson gson;
	ServerLocatorService locatorService;



    /**
     * @see HttpServlet#HttpServlet()
     */
    public EventTrackerServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {				
//		String username = request.getParameter("username");
//		String lat = request.getParameter("lat");
//		String lng = request.getParameter("lon");
//		
//		//check if null?
//		Double latD = Double.valueOf(lat);
//		Double lngD = Double.valueOf(lng);
//
//		
//		PingResponse res = new GeoServiceFacade(
//				locatorService.getLocationShard(latD, lngD))
//				.execute(new PingAction(latD, lngD, username));		
//		
//		List<Event> events = res.getEvents();
//		if (res.canCreateEvent()){
//			events.add(new Event(-1, null, null));
//		}
//		
//		gson = new GsonBuilder().create();
//		gson.toJson(res, new TypeToken<List<Event>>(){}.getType());
	
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//add user functionality
		factory = new HessianProxyFactory();
		addr = InetAddress.getLocalHost();

		locatorService = getServiceLocator();
		
		String username = request.getParameter("mUser");
		String name = request.getParameter("mName");
		String password = request.getParameter("mPass");

		GeoService geoService = new GeoServiceFacade(
				locatorService.getUserShard(username));

		geoService.execute(new AddUserAction(username, name, password));
		
		gson = new GsonBuilder().create();
		gson.toJson(username);
	
	}
	
	private ServerLocatorService getServiceLocator()
			throws MalformedURLException {
		
		String locatorURL = ServerLocator.getURL(addr.getHostName(),
				ServerLocator.SERVER_LOCATOR_PORT);
		return (ServerLocatorService) factory.create(
				ServerLocatorService.class, locatorURL);
	}

}
