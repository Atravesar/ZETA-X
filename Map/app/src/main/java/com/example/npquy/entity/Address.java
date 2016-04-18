package com.example.npquy.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * Created by npquy on 3/17/2016.
 */
public class Address implements Serializable{

    private String Outcode;
    private String Postcode;
    private String Fulladdress;
    private String Category;
    private String Icon_Path;
    private Double Latitude;
    private Double Longitude;

    public Address() {
    }
    public Address(String name, String address){
        this.Fulladdress = name;
        this.Postcode = address;
    }

    public String getOutcode() {
        return Outcode;
    }

    public void setOutcode(String outcode) {
        Outcode = outcode;
    }

    public String getPostcode() {
        return Postcode;
    }

    public void setPostcode(String postcode) {
        Postcode = postcode;
    }

    public String getFulladdress() {
        return Fulladdress;
    }

    public void setFulladdress(String fulladdress) {
        Fulladdress = fulladdress;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getIcon_Path() {
        return Icon_Path;
    }

    public void setIcon_Path(String icon_Path) {
        Icon_Path = icon_Path;
    }

    public Double getLatitude() {
        return Latitude;
    }

    public void setLatitude(Double latitude) {
        Latitude = latitude;
    }

    public Double getLongitude() {
        return Longitude;
    }

    public void setLongitude(Double longitude) {
        Longitude = longitude;
    }

    @Override
    public String toString() {
        return "Address{" +
                "Outcode='" + Outcode + '\'' +
                ", Postcode='" + Postcode + '\'' +
                ", Fulladdress='" + Fulladdress + '\'' +
                ", Category='" + Category + '\'' +
                ", Icon_Path='" + Icon_Path + '\'' +
                ", Latitude=" + Latitude +
                ", Longitude=" + Longitude +
                '}';
    }

    public Address clone() {
        Address a = new Address();
        a.setCategory(getCategory());
        a.setFulladdress(getFulladdress());
        a.setIcon_Path(getIcon_Path());
        a.setLatitude(getLatitude());
        a.setLongitude(getLongitude());
        a.setOutcode(getOutcode());
        a.setPostcode(getPostcode());
        return a;
    }
}
