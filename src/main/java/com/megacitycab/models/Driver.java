package com.megacitycab.models;

import java.io.Serializable;

public class Driver implements Serializable {
    private int driverId;
    private String driverName;
    private String driverLicense;
    private String phoneNumber;
    private String driverStatus;
    private String username;  // ✅ Added for authentication
    private String password;  // ✅ Added for authentication

    // ✅ Default Constructor (Required for frameworks & ORM)
    public Driver() {}

    // ✅ Constructor for Fetching Driver from Database (Without Login Details)
    public Driver(int driverId, String driverName, String driverLicense, String phoneNumber, String driverStatus) {
        this.driverId = driverId;
        this.driverName = driverName;
        this.driverLicense = driverLicense;
        this.phoneNumber = phoneNumber;
        this.driverStatus = driverStatus;
    }

    // ✅ Constructor for Adding a New Driver (Without Login Credentials)
    public Driver(String driverName, String driverLicense, String phoneNumber, String driverStatus) {
        this.driverName = driverName;
        this.driverLicense = driverLicense;
        this.phoneNumber = phoneNumber;
        this.driverStatus = driverStatus;
    }

    // ✅ Constructor for Adding a New Driver (With Login Credentials)
    public Driver(String driverName, String driverLicense, String phoneNumber, String driverStatus, String username, String password) {
        this.driverName = driverName;
        this.driverLicense = driverLicense;
        this.phoneNumber = phoneNumber;
        this.driverStatus = driverStatus;
        this.username = username;
        this.password = password;
    }

    // ✅ Constructor for Fetching Driver with Login Credentials
    public Driver(int driverId, String driverName, String driverLicense, String phoneNumber, String driverStatus, String username, String password) {
        this.driverId = driverId;
        this.driverName = driverName;
        this.driverLicense = driverLicense;
        this.phoneNumber = phoneNumber;
        this.driverStatus = driverStatus;
        this.username = username;
        this.password = password;
    }

    // ✅ Getters and Setters
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

    public String getUsername() { return username; } // ✅ Getter for username
    public void setUsername(String username) { this.username = username; } // ✅ Setter for username

    public String getPassword() { return password; } // ✅ Getter for password
    public void setPassword(String password) { this.password = password; } // ✅ Setter for password
}