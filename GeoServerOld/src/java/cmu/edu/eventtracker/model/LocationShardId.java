/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cmu.edu.eventtracker.model;

import java.io.Serializable;
import javax.persistence.Embeddable;

/**
 *
 * @author anarhuseynov
 */
@Embeddable
public class LocationShardId implements Serializable {
    
    private Double latmin;
    private Double latmax;
    private Double lngmin;
    private Double lngmax;
    private String jdbc;

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LocationShardId other = (LocationShardId) obj;
        if (this.latmin != other.latmin && (this.latmin == null || !this.latmin.equals(other.latmin))) {
            return false;
        }
        if (this.latmax != other.latmax && (this.latmax == null || !this.latmax.equals(other.latmax))) {
            return false;
        }
        if (this.lngmin != other.lngmin && (this.lngmin == null || !this.lngmin.equals(other.lngmin))) {
            return false;
        }
        if (this.lngmax != other.lngmax && (this.lngmax == null || !this.lngmax.equals(other.lngmax))) {
            return false;
        }
        if ((this.jdbc == null) ? (other.jdbc != null) : !this.jdbc.equals(other.jdbc)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + (this.latmin != null ? this.latmin.hashCode() : 0);
        hash = 83 * hash + (this.latmax != null ? this.latmax.hashCode() : 0);
        hash = 83 * hash + (this.lngmin != null ? this.lngmin.hashCode() : 0);
        hash = 83 * hash + (this.lngmax != null ? this.lngmax.hashCode() : 0);
        hash = 83 * hash + (this.jdbc != null ? this.jdbc.hashCode() : 0);
        return hash;
    }

    public String getJdbc() {
        return jdbc;
    }

    public void setJdbc(String jdbc) {
        this.jdbc = jdbc;
    }

    public Double getLatmax() {
        return latmax;
    }

    public void setLatmax(Double latmax) {
        this.latmax = latmax;
    }

    public Double getLatmin() {
        return latmin;
    }

    public void setLatmin(Double latmin) {
        this.latmin = latmin;
    }

    public Double getLngmax() {
        return lngmax;
    }

    public void setLngmax(Double lngmax) {
        this.lngmax = lngmax;
    }

    public Double getLngmin() {
        return lngmin;
    }

    public void setLngmin(Double lngmin) {
        this.lngmin = lngmin;
    }
    
    
    
}
