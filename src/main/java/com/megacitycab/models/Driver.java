package com.megacitycab.models;

public class Driver {
    private int driverId;
    private String driverName;
    private String driverLicense;
    private String phoneNumber;
    private String driverStatus;

    // Constructor for fetching from the database
    public Driver(int driverId, String driverName, String driverLicense, String phoneNumber, String driverStatus) {
        this.driverId = driverId;
        this.driverName = driverName;
        this.driverLicense = driverLicense;
        this.phoneNumber = phoneNumber;
        this.driverStatus = driverStatus;
    }

    // Constructor for adding a new driver
    public Driver(String driverName, String driverLicense, String phoneNumber, String driverStatus) {
        this.driverName = driverName;
        this.driverLicense = driverLicense;
        this.phoneNumber = phoneNumber;
        this.driverStatus = driverStatus;
    }

    // Getters and Setters
    public int getDriverId() { return driverId; }
    public void setDriverId(int driverId) { this.driverId = driverId; }

    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }

    public String getDriverLicense() { return driverLicense; }
    public void setDriverLicense(String driverLicense) { this.driverLicense = driverLicense; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getDriverStatus() { return driverStatus; }
    public void setDriverStatus(String driverStatus) { this.driverStatus = driverStatus; }
}
