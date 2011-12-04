package edu.cmu.eventtracker.actionhandler;

import java.util.UUID;

public class Helper {
	public static String toString(UUID uuid) {
		if (uuid == null) {
			return null;
		}
		return uuid.toString();
	}

	public static UUID fromString(String uuid) {
		if (uuid == null) {
			return null;
		}
		return UUID.fromString(uuid);
	}

}
