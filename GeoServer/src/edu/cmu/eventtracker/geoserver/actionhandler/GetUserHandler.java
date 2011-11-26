package edu.cmu.eventtracker.geoserver.actionhandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import edu.cmu.eventtracker.geoserver.action.GetUserAction;
import edu.cmu.eventtracker.geoserver.dto.User;

public class GetUserHandler implements ActionHandler<GetUserAction, User> {

	@Override
	public User performAction(GetUserAction action, ActionContext context) {
		GeoServiceContext geoContext = (GeoServiceContext) context;
		try {
			PreparedStatement prepareStatement = geoContext.getUsersConnection()
					.prepareStatement("Select username, name from users where username=?");
			prepareStatement.setString(1, action.getUsername());
			prepareStatement.execute();
			ResultSet rs = prepareStatement.getResultSet();
			while (rs.next()) {
				User user = new User();
				user.setName(rs.getString("name"));
				user.setUsername(rs.getString("username"));
				return user;
			}
			return null;
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
	}

}
