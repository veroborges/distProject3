package edu.cmu.eventtracker.dto;

import java.io.Serializable;

public class ShardResponse implements Serializable {

	private String master;
	private String slave;

	public ShardResponse() {
	}

	public ShardResponse(String master, String slave) {
		this.master = master;
		this.slave = slave;
	}

	public String getMaster() {
		return master;
	}
	public void setMaster(String master) {
		this.master = master;
	}
	public String getSlave() {
		return slave;
	}
	public void setSlave(String slave) {
		this.slave = slave;
	}
}
