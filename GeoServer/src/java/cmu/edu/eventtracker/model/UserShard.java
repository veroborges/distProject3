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
public class UserShard implements Serializable {

    @EmbeddedId
    private UserShardId id;

    public UserShardId getId() {
        return id;
    }

    public void setId(UserShardId id) {
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
        final UserShard other = (UserShard) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 13 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }
    
    
}
