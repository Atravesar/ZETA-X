package com.example.npquy.entity;

/**
 * Created by ITACHI on 3/27/2016.
 */
public class JourneyHistory {
    Integer JobPartID;
    String Status;
    String Contact;
    String PickupAddress;
    String DropoffAddress;
    String TotalFare;
    String Notes;
    String PickupDateTime;
    String ViaLocations;
    String FlightNumber;
    Double ExpFlightArrival;
    String ChildSeat;
    Integer Passengers;
    Integer Suitcases;
    String MeetAndGreet;
    String PaymentMethod;
    String JourneyType;
    String VehicleType;

    public JourneyHistory() {
    }


    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getContact() {
        return Contact;
    }

    public void setContact(String contact) {
        Contact = contact;
    }

    public String getPickupAddress() {
        return PickupAddress;
    }

    public void setPickupAddress(String pickupAddress) {
        PickupAddress = pickupAddress;
    }

    public String getDropoffAddress() {
        return DropoffAddress;
    }

    public void setDropoffAddress(String dropoffAddress) {
        DropoffAddress = dropoffAddress;
    }

    public Integer getJobPartID() {
        return JobPartID;
    }

    public void setJobPartID(Integer jobPartID) {
        JobPartID = jobPartID;
    }

    public String getTotalFare() {
        return TotalFare;
    }

    public void setTotalFare(String totalFare) {
        TotalFare = totalFare;
    }

    public String getNotes() {
        return Notes;
    }

    public void setNotes(String notes) {
        Notes = notes;
    }

    public String getPickupDateTime() {
        return PickupDateTime;
    }

    public void setPickupDateTime(String pickupDateTime) {
        PickupDateTime = pickupDateTime;
    }

    public String getViaLocations() {
        return ViaLocations;
    }

    public void setViaLocations(String viaLocations) {
        ViaLocations = viaLocations;
    }

    public String getFlightNumber() {
        return FlightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        FlightNumber = flightNumber;
    }

    public Double getExpFlightArrival() {
        return ExpFlightArrival;
    }

    public void setExpFlightArrival(Double expFlightArrival) {
        ExpFlightArrival = expFlightArrival;
    }

    public String getChildSeat() {
        return ChildSeat;
    }

    public void setChildSeat(String childSeat) {
        ChildSeat = childSeat;
    }

    public Integer getPassengers() {
        return Passengers;
    }

    public void setPassengers(Integer passengers) {
        Passengers = passengers;
    }

    public Integer getSuitcases() {
        return Suitcases;
    }

    public void setSuitcases(Integer suitcases) {
        Suitcases = suitcases;
    }

    public String getMeetAndGreet() {
        return MeetAndGreet;
    }

    public void setMeetAndGreet(String meetAndGreet) {
        MeetAndGreet = meetAndGreet;
    }

    public String getPaymentMethod() {
        return PaymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        PaymentMethod = paymentMethod;
    }

    public String getJourneyType() {
        return JourneyType;
    }

    public void setJourneyType(String journeyType) {
        JourneyType = journeyType;
    }

    public String getVehicleType() {
        return VehicleType;
    }

    public void setVehicleType(String vehicleType) {
        VehicleType = vehicleType;
    }

    @Override
    public String toString() {
        return "JourneyHistory{" +
                "JobPartID=" + JobPartID +
                ", Status='" + Status + '\'' +
                ", Contact='" + Contact + '\'' +
                ", PickupAddress='" + PickupAddress + '\'' +
                ", DropoffAddress='" + DropoffAddress + '\'' +
                ", TotalFare='" + TotalFare + '\'' +
                ", Notes='" + Notes + '\'' +
                ", PickupDateTime='" + PickupDateTime + '\'' +
                ", ViaLocations='" + ViaLocations + '\'' +
                ", FlightNumber='" + FlightNumber + '\'' +
                ", ExpFlightArrival=" + ExpFlightArrival +
                ", ChildSeat='" + ChildSeat + '\'' +
                ", Passengers=" + Passengers +
                ", Suitcases=" + Suitcases +
                ", MeetAndGreet='" + MeetAndGreet + '\'' +
                ", PaymentMethod='" + PaymentMethod + '\'' +
                ", JourneyType='" + JourneyType + '\'' +
                ", VehicleType='" + VehicleType + '\'' +
                '}';
    }
}
