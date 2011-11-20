/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.cmu.eventracker.geoservice;

import java.util.List;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;

/**
 *
 * @author anarhuseynov
 */
@WebService(serviceName = "GeoServer")
public class GeoServer {
    
    /** This is a sample web service operation */
    @WebMethod(operationName = "ping")
    public void ping(@WebParam(name = "name") double lat, double lng, String username) {
        
    }
    
    public void createEvent(String eventName, double lat, double lng, String username) {
        
    }
    
    public List<Event> getEvents(double lat, double lng, String username) {
        return null;
    }
}
