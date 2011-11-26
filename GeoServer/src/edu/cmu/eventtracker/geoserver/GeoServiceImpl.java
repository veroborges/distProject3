package edu.cmu.eventtracker.geoserver;

import java.sql.SQLException;

import javax.servlet.ServletException;

import com.caucho.hessian.server.HessianServlet;

import edu.cmu.eventtracker.geoserver.action.Action;
import edu.cmu.eventtracker.geoserver.actionhandler.GeoServiceContext;

public class GeoServiceImpl extends HessianServlet implements GeoService {

	private GeoServiceContext context;

	@Override
	public void init() throws ServletException {
		super.init();
		context = new GeoServiceContext(this);

	}
	@Override
	public <A extends Action<R>, R> R execute(A action) {
		boolean commit = true;
		try {
			return context.execute(action);
		} catch (RuntimeException ex) {
			commit = false;
			throw ex;
		} finally {
			if (commit) {
				try {
					context.getUsersConnection().commit();
					context.getLocationsConnection().commit();
				} catch (SQLException e) {
					throw new IllegalStateException(e);
				}
			} else {
				try {
					context.getUsersConnection().rollback();
					context.getLocationsConnection().rollback();
				} catch (SQLException e) {
					throw new IllegalStateException(e);
				}
			}
		}
	}

}
