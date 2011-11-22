/**
 * 
 */
package edu.cmu.eventtracker.tests;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import java.util.List;
import java.util.Random;

import java.sql.PreparedStatement;
import org.junit.Test;

import com.caucho.hessian.client.HessianProxyFactory;

import edu.cmu.eventtracker.geoserver.ServerLocatorService;

public class BasicUnitTest {
	
	public String driver = "org.apache.derby.jdbc.EmbeddedDriver";
	public String protocol = "jdbc:derby:";
	Random gen = new Random();

	public boolean addUser(String username, String name, String pass){
		ResultSet rs = null;

		try{	
			Connection conn = DriverManager.getConnection(protocol
				+ "usersDB;create=true", null);
			
			String url = "http://localhost:9991/";
			
			HessianProxyFactory factory = new HessianProxyFactory();
			ServerLocatorService locatorService = (ServerLocatorService) factory.create(ServerLocatorService.class, url + ServerLocatorService.class.getSimpleName());
			locatorService.getUserShard("username"); 
			
			PreparedStatement selectUser = conn.prepareStatement("selec * from users where username= ?");
			PreparedStatement createUser = conn.prepareStatement("insert into users values (?, ?, ?)");
			
			//check if user already exists
			selectUser.setString(1, username);
			selectUser.execute();
	
			if (rs.next()){
				System.out.println("User" + username + "already exists in database");
				return false;
			}
			//create new user
			else{
				createUser.setString(1, username);
				createUser.setString(2, name);
				createUser.setString(3, pass);
				createUser.executeUpdate();
				System.out.println("Created user" + username + "in user database");
				return true;
		}
		}catch (SQLException e) {
			throw new IllegalStateException(e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
						throw new IllegalStateException(e);
				}
			}
		}
	}
	
	public void addUserShard(int nodeid, String hostname){
		try {
			Connection conn = DriverManager.getConnection(protocol
					+ "shardsDB;create=true", null);
			
			//statement to get all of a user's locations
			PreparedStatement createShard = conn
					.prepareStatement("insert into usershards values(?, ?) ");
		
			createShard.setInt(1, nodeid);
			createShard.setString(2, hostname);
			createShard.execute();
			
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
	}
	public void testCreateUsers(int userNumber) {	
		int counter = 0;
		String username = "";
		String password = "testpass";
		String name = "testuser";
		
	
		while(counter < userNumber){
			username = Integer.toString(gen.nextInt()) + Integer.toString(gen.nextInt());
				
			if (addUser(username, name, password)){
					counter++;
			}
			}
	
	}
}
