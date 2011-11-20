package cmu.edu.eventtracker.model;

import java.io.Serializable;
import javax.persistence.Embeddable;

@Embeddable
public class UserEventId implements Serializable {

	private String username;
	private long eventId;
	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * @param username
	 *            the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	/**
	 * @return the eventId
	 */
	public long getEventId() {
		return eventId;
	}
	/**
	 * @param eventId
	 *            the eventId to set
	 */
	public void setEventId(long eventId) {
		this.eventId = eventId;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (eventId ^ (eventId >>> 32));
		result = prime * result
				+ ((username == null) ? 0 : username.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserEventId other = (UserEventId) obj;
		if (eventId != other.eventId)
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}

}
