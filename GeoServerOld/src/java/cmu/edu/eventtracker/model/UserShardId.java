package cmu.edu.eventtracker.model;

import java.io.Serializable;
import javax.persistence.Embeddable;

@Embeddable
public class UserShardId implements Serializable {

    private String jdbc;
    private long nodeId;

    public String getJdbc() {
        return jdbc;
    }

    public void setJdbc(String jdbc) {
        this.jdbc = jdbc;
    }

    public long getNodeId() {
        return nodeId;
    }

    public void setNodeId(long nodeId) {
        this.nodeId = nodeId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (nodeId ^ (nodeId >>> 32));
        result = prime * result
                + ((jdbc == null) ? 0 : jdbc.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        UserShardId other = (UserShardId) obj;
        if (nodeId != other.nodeId) {
            return false;
        }
        if (jdbc == null) {
            if (other.jdbc != null) {
                return false;
            }
        } else if (!jdbc.equals(other.jdbc)) {
            return false;
        }
        return true;
    }
}
