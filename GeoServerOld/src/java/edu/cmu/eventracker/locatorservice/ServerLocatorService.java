/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.cmu.eventracker.locatorservice;

import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;

/**
 *
 * @author anarhuseynov
 */
@WebService(serviceName = "ServerLocatorService")
public class ServerLocatorService {

    public String getGeoServer(double lat, double lng) {
        return null;
    }
    
    public String getUserServer(String username) {
        return null;
    }
}
