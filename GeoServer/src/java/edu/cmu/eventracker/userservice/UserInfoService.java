/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.cmu.eventracker.userservice;

import java.util.List;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;

/**
 *
 * @author anarhuseynov
 */
@WebService(serviceName = "UserInfoService")
public class UserInfoService {

    
    public List<Location> getUserLocations(String username) {
        return null;
    }
}
