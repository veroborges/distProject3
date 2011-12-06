package edu.cmu.eventtracker.actionhandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.cmu.eventtracker.action.AddUserAction;

public class AddUserHandler implements ActionHandler<AddUserAction, Boolean> {

	@Override
	public Boolean performAction(AddUserAction action, ActionContext context) {
		ResultSet rs = null;
		GeoServiceContext geoContext = (GeoServiceContext) context;

		try {
			PreparedStatement selectUser = geoContext.getUsersConnection()
					.prepareStatement(
							"select username from users where username= ?");
			PreparedStatement createUser = geoContext
					.getUsersConnection()
					.prepareStatement(
							"insert into users(username, name, password) values (?, ?, ?)");

			// check if user already exists
			selectUser.setString(1, action.getUsername());
			selectUser.execute();
			rs = selectUser.getResultSet();
			if (rs.next()) {
				Logger.getLogger("GeoServer").log(
						Level.INFO,
						"User "
								+ action.getUsername()
								+ " already exists in database: "
								+ geoContext.getService().getUrl()
								+ " as "
								+ (geoContext.getService().isMaster()
										? "master"
										: "slave"));
				return false;
			}
			// create new user
			else {
				createUser.setString(1, action.getUsername());
				createUser.setString(2, action.getName());
				createUser.setString(3, action.getPassword());
				createUser.executeUpdate();

				Logger.getLogger("GeoServer").log(
						Level.INFO,
						"Inserting user to "
								+ geoContext.getService().getUrl()
								+ " as "
								+ (geoContext.getService().isMaster()
										? "master"
										: "slave") + ": "
								+ action.getUsername());
				return true;
			}
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
	}
}
