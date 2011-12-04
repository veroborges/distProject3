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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((master == null) ? 0 : master.hashCode());
		result = prime * result + ((slave == null) ? 0 : slave.hashCode());
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
		ShardResponse other = (ShardResponse) obj;
		if (master == null) {
			if (other.master != null)
				return false;
		} else if (!master.equals(other.master))
			return false;
		if (slave == null) {
			if (other.slave != null)
				return false;
		} else if (!slave.equals(other.slave))
			return false;
		return true;
	}

}
