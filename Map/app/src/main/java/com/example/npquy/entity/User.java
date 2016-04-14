package com.example.npquy.entity;

/**
 * Created by npquy on 3/22/2016.
 */
public class User {
    private String CusID;
    private String Name;
    private String Mobile;
    private String Email;
    private String DeviceID;

    public User() {
    }

    public String getCusID() {
        return CusID;
    }

    public void setCusID(String cusID) {
        CusID = cusID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String mobile) {
        Mobile = mobile;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getDeviceID() {
        return DeviceID;
    }

    public void setDeviceID(String deviceID) {
        DeviceID = deviceID;
    }

    @Override
    public String toString() {
        return "User{" +
                "CusID='" + CusID + '\'' +
                ", Name='" + Name + '\'' +
                ", Mobile='" + Mobile + '\'' +
                ", Email='" + Email + '\'' +
                ", DeviceID='" + DeviceID + '\'' +
                '}';
    }
}
