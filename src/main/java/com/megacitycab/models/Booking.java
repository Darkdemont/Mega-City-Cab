package com.megacitycab.models;

import java.io.Serializable;

public class Booking implements Serializable {
    private static final long serialVersionUID = 1L;

    private int bookingId;
    private int customerId;
    private String pickupLocation;
    private String destination;
    private String carType;
    private String dateTime;
    private double distanceKm;
    private double totalFare;
    private String status;
    private int driverId;


    public Booking() {}


    public Booking(int bookingId, int customerId, String pickupLocation, String destination,
                   String carType, String dateTime, double distanceKm, double totalFare,
                   String status, int driverId) {
        this.bookingId = bookingId;
        this.customerId = customerId;
        this.pickupLocation = pickupLocation;
        this.destination = destination;
        this.carType = carType;
        this.dateTime = dateTime;
        this.distanceKm = distanceKm;
        this.totalFare = totalFare;
        this.status = status;
        this.driverId = driverId;
    }


    public Booking(int customerId, String pickupLocation, String destination, String carType,
                   String dateTime, double distanceKm, String status) {
        this.customerId = customerId;
        this.pickupLocation = pickupLocation;
        this.destination = destination;
        this.carType = carType;
        this.dateTime = dateTime;
        this.distanceKm = distanceKm;
        this.totalFare = 0.0;   // ✅ Default total fare to 0
        this.status = status;
        this.driverId = -1;     // ✅ Default driver ID to -1 (unassigned)
    }


    public Booking(int customerId, String pickupLocation, String destination, String carType,
                   String dateTime, double distanceKm, String status, int driverId) {
        this.customerId = customerId;
        this.pickupLocation = pickupLocation;
        this.destination = destination;
        this.carType = carType;
        this.dateTime = dateTime;
        this.distanceKm = distanceKm;
        this.totalFare = 0.0;
        this.status = status;
        this.driverId = driverId; // ✅ Allows assigning a driver at booking time
    }






    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public String getPickupLocation() { return pickupLocation; }
    public void setPickupLocation(String pickupLocation) { this.pickupLocation = pickupLocation; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public String getCarType() { return carType; }
    public void setCarType(String carType) { this.carType = carType; }

    public String getDateTime() { return dateTime; }
    public void setDateTime(String dateTime) { this.dateTime = dateTime; }

    public double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(double distanceKm) { this.distanceKm = distanceKm; }

    public double getTotalFare() { return totalFare; }
    public void setTotalFare(double totalFare) { this.totalFare = totalFare; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getDriverId() { return driverId; }
    public void setDriverId(int driverId) { this.driverId = driverId; }


    @Override
    public String toString() {
        return "Booking{" +
                "bookingId=" + bookingId +
                ", customerId=" + customerId +
                ", pickupLocation='" + pickupLocation + '\'' +
                ", destination='" + destination + '\'' +
                ", carType='" + carType + '\'' +
                ", dateTime='" + dateTime + '\'' +
                ", distanceKm=" + distanceKm +
                ", totalFare=" + totalFare +
                ", status='" + status + '\'' +
                ", driverId=" + (driverId == -1 ? "Unassigned" : driverId) + // Display "Unassigned" if driverId = -1
                '}';
    }
}