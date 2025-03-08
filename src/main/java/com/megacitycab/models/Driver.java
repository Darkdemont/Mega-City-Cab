package com.megacitycab.models;

import java.io.Serializable;

public class Driver implements Serializable {
    private int driverId;
    private String driverName;
    private String driverLicense;
    private String phoneNumber;
    private String driverStatus;
    private int userId;
    private String username;
    private String password;
    private String email; // ✅ Added email field

    public Driver() {}

    public Driver(int driverId, String driverName, String driverLicense, String phoneNumber, String driverStatus) {
        this.driverId = driverId;
        this.driverName = driverName;
        this.driverLicense = driverLicense;
        this.phoneNumber = phoneNumber;
        this.driverStatus = driverStatus;
    }

    public Driver(String driverName, String driverLicense, String phoneNumber, String driverStatus) {
        this.driverName = driverName;
        this.driverLicense = driverLicense;
        this.phoneNumber = phoneNumber;
        this.driverStatus = driverStatus;
    }

    public Driver(String driverName, String driverLicense, String phoneNumber, String driverStatus, String username, String password) {
        this.driverName = driverName;
        this.driverLicense = driverLicense;
        this.phoneNumber = phoneNumber;
        this.driverStatus = driverStatus;
        this.username = username;
        this.password = password;
    }

    public Driver(int driverId, String driverName, String driverLicense, String phoneNumber, String driverStatus, String username, String password) {
        this.driverId = driverId;
        this.driverName = driverName;
        this.driverLicense = driverLicense;
        this.phoneNumber = phoneNumber;
        this.driverStatus = driverStatus;
        this.username = username;
        this.password = password;
    }

    public Driver(int driverId, String driverName, String driverLicense, String phoneNumber, String driverStatus, int userId) {
        this.driverId = driverId;
        this.driverName = driverName;
        this.driverLicense = driverLicense;
        this.phoneNumber = phoneNumber;
        this.driverStatus = driverStatus;
        this.userId = userId;
    }

    // ✅ New Constructor with Email
    public Driver(int driverId, String driverName, String driverLicense, String phoneNumber, String driverStatus, int userId, String email) {
        this.driverId = driverId;
        this.driverName = driverName;
        this.driverLicense = driverLicense;
        this.phoneNumber = phoneNumber;
        this.driverStatus = driverStatus;
        this.userId = userId;
        this.email = email;
    }

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

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    // ✅ Added getEmail() Method
    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // ✅ Added getMobile() Method (returns phoneNumber)
    public String getMobile() {
        return this.phoneNumber;
    }
}
