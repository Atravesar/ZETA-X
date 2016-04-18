package com.example.npquy.entity;

import java.io.Serializable;

/**
 *
 * @author npquy
 *
 */
public class Location{

    double Lat;
    double Lgn;

    // The constructor
    public Location(double latitude, double longitude) {
        super();
        this.Lat = latitude;
        this.Lgn = longitude;
    }
    public Location() {
        super();
    }

    public double getLat() {
        return Lat;
    }

    public void setLat(double lat) {
        Lat = lat;
    }

    public double getLgn() {
        return Lgn;
    }

    public void setLgn(double lgn) {
        Lgn = lgn;
    }
}
