package com.example.npquy.entity;

/**
 * Created by npquy on 3/31/2016.
 */
public class RetrieveQuoteResult {
    Integer vehTypeID;
    Integer rtn_routedistance;
    Double routedistance;
    Integer rtn_traveltime;
    Double traveltime;
    String vehType;
    String totalfare;
    String fare;
    String returnfare;
    Double viaLat;
    Double viaLong;
    Double doLat;
    Double doLong;
    Double pkLat;
    Double pkLong;
    String bookingRef;
    Boolean InServiceArea;

    public RetrieveQuoteResult() {
    }

    public Integer getVehTypeID() {
        return vehTypeID;
    }

    public void setVehTypeID(Integer vehTypeID) {
        this.vehTypeID = vehTypeID;
    }

    public Integer getRtn_routedistance() {
        return rtn_routedistance;
    }

    public void setRtn_routedistance(Integer rtn_routedistance) {
        this.rtn_routedistance = rtn_routedistance;
    }

    public Double getRoutedistance() {
        return routedistance;
    }

    public void setRoutedistance(Double routedistance) {
        this.routedistance = routedistance;
    }

    public Integer getRtn_traveltime() {
        return rtn_traveltime;
    }

    public void setRtn_traveltime(Integer rtn_traveltime) {
        this.rtn_traveltime = rtn_traveltime;
    }

    public Double getTraveltime() {
        return traveltime;
    }

    public void setTraveltime(Double traveltime) {
        this.traveltime = traveltime;
    }

    public String getVehType() {
        return vehType;
    }

    public void setVehType(String vehType) {
        this.vehType = vehType;
    }

    public String getTotalfare() {
        return totalfare;
    }

    public void setTotalfare(String totalfare) {
        this.totalfare = totalfare;
    }

    public String getFare() {
        return fare;
    }

    public void setFare(String fare) {
        this.fare = fare;
    }

    public String getReturnfare() {
        return returnfare;
    }

    public void setReturnfare(String returnfare) {
        this.returnfare = returnfare;
    }

    public Double getViaLat() {
        return viaLat;
    }

    public void setViaLat(Double viaLat) {
        this.viaLat = viaLat;
    }

    public Double getViaLong() {
        return viaLong;
    }

    public void setViaLong(Double viaLong) {
        this.viaLong = viaLong;
    }

    public Double getDoLat() {
        return doLat;
    }

    public void setDoLat(Double doLat) {
        this.doLat = doLat;
    }

    public Double getDoLong() {
        return doLong;
    }

    public void setDoLong(Double doLong) {
        this.doLong = doLong;
    }

    public Double getPkLat() {
        return pkLat;
    }

    public void setPkLat(Double pkLat) {
        this.pkLat = pkLat;
    }

    public Double getPkLong() {
        return pkLong;
    }

    public void setPkLong(Double pkLong) {
        this.pkLong = pkLong;
    }

    public String getBookingRef() {
        return bookingRef;
    }

    public void setBookingRef(String bookingRef) {
        this.bookingRef = bookingRef;
    }

    public Boolean getInServiceArea() {
        return InServiceArea;
    }

    public void setInServiceArea(Boolean inServiceArea) {
        InServiceArea = inServiceArea;
    }

    @Override
    public String toString() {
        return "RetrieveQuoteResult{" +
                "vehTypeID=" + vehTypeID +
                ", rtn_routedistance=" + rtn_routedistance +
                ", routedistance=" + routedistance +
                ", rtn_traveltime=" + rtn_traveltime +
                ", traveltime=" + traveltime +
                ", vehType='" + vehType + '\'' +
                ", totalfare='" + totalfare + '\'' +
                ", fare='" + fare + '\'' +
                ", returnfare='" + returnfare + '\'' +
                ", viaLat=" + viaLat +
                ", viaLong=" + viaLong +
                ", doLat=" + doLat +
                ", doLong=" + doLong +
                ", pkLat=" + pkLat +
                ", pkLong=" + pkLong +
                ", bookingRef='" + bookingRef + '\'' +
                ", InServiceArea=" + InServiceArea +
                '}';
    }
}
