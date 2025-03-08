package com.megacitycab.models;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private int userId;  // ✅ Renamed to match database column `user_id`
    private String username;
    private String email;
    private String password;
    private String role;

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

    // ✅ Getters and Setters
    public int getUserId() { return userId; }  // ✅ Use `getUserId()` instead of `getId()`
    public void setUserId(int userId) { this.userId = userId; }  // ✅ Use `setUserId()`

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    // ✅ ToString for Debugging
    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
