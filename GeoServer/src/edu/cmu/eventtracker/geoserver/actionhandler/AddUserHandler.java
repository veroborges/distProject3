package edu.cmu.eventtracker.geoserver.actionhandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import edu.cmu.eventtracker.geoserver.action.AddUserAction;

public class AddUserHandler
		implements
			ActionHandler<AddUserAction, Boolean> {

	@Override
	public Boolean performAction(AddUserAction action, ActionContext context) {
		ResultSet rs = null;
		GeoServiceContext geoContext = (GeoServiceContext) context;

		try {
			PreparedStatement selectUser = geoContext.getUsersConnection()
					.prepareStatement("select username from users where username= ?");
			PreparedStatement createUser = geoContext.getUsersConnection()
					.prepareStatement("insert into users(username, name, password) values (?, ?, ?)");

			// check if user already exists
			selectUser.setString(1, action.getUsername());
			selectUser.execute();
			rs = selectUser.getResultSet();
			if (rs.next()) {
				System.out.println("User " + action.getUsername()
						+ " already exists in database");
				return false;
			}
			// create new user
			else {
				createUser.setString(1, action.getUsername());
				createUser.setString(2, action.getName());
				createUser.setString(3, action.getPassword());
				createUser.executeUpdate();

				System.out.println("Created user " + action.getUsername()
						+ " in user database");
				return true;
			}
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
	}
}
