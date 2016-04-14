package com.example.npquy.entity;

/**
 * Created by ITACHI on 3/30/2016.
 */
public class NearestDriver {
    Integer DriverID;
    String DriverNumber;
    String FirstName;
    String LastName;
    Double Distance;
    Double TravelTime;
    Location CurrentPosition;

    public NearestDriver() {
    }

    public Integer getDriverID() {
        return DriverID;
    }

    public void setDriverID(Integer driverID) {
        DriverID = driverID;
    }

    public String getDriverNumber() {
        return DriverNumber;
    }

    public void setDriverNumber(String driverNumber) {
        DriverNumber = driverNumber;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public Double getDistance() {
        return Distance;
    }

    public void setDistance(Double distance) {
        Distance = distance;
    }

    public Double getTravelTime() {
        return TravelTime;
    }

    public void setTravelTime(Double travelTime) {
        TravelTime = travelTime;
    }

    public Location getCurrentPosition() {
        return CurrentPosition;
    }

    public void setCurrentPosition(Location currentPosition) {
        CurrentPosition = currentPosition;
    }

    @Override
    public String toString() {
        return "NearestDriver{" +
                "DriverID=" + DriverID +
                ", DriverNumber='" + DriverNumber + '\'' +
                ", FirstName='" + FirstName + '\'' +
                ", LastName='" + LastName + '\'' +
                ", Distance=" + Distance +
                ", TravelTime=" + TravelTime +
                ", CurrentPosition=" + CurrentPosition +
                '}';
    }
}
