package com.megacitycab.models;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private int userId;
    private String username;
    private String email;
    private String password;
    private String role;
    private String mobile;  // ✅ Added Mobile Number
    private String nic;     // ✅ Added NIC
    private String address; // ✅ Added Address

    // ✅ Default Constructor
    public User() {}

    // ✅ Constructor for Creating a New User (Without userId)
    public User(String username, String email, String password, String role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // ✅ Constructor for Fetching User Data (With userId)
    public User(int userId, String username, String email, String password, String role) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // ✅ Constructor Including NIC, Address, and Mobile
    public User(int userId, String username, String email, String password, String role, String mobile, String nic, String address) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.mobile = mobile;
        this.nic = nic;
        this.address = address;
    }

    // ✅ Getters and Setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getMobile() { return mobile; }  // ✅ Getter for Mobile
    public void setMobile(String mobile) { this.mobile = mobile; }  // ✅ Setter for Mobile

    public String getNic() { return nic; }  // ✅ Getter for NIC
    public void setNic(String nic) { this.nic = nic; }  // ✅ Setter for NIC

    public String getAddress() { return address; }  // ✅ Getter for Address
    public void setAddress(String address) { this.address = address; }  // ✅ Setter for Address

    // ✅ ToString for Debugging
    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", mobile='" + mobile + '\'' +
                ", nic='" + nic + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
