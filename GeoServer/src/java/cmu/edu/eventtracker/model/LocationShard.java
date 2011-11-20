/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cmu.edu.eventtracker.model;

import java.io.Serializable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 *
 * @author anarhuseynov
 */
@Entity
public class LocationShard implements Serializable {
    @EmbeddedId
    private LocationShardId id;

    public LocationShardId getId() {
        return id;
    }

    public void setId(LocationShardId id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LocationShard other = (LocationShard) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }
    
    
}
