package com.megacitycab.dao;

import com.megacitycab.models.Driver;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DriverDAO {
    private Connection conn;

    public DriverDAO() {
        conn = DatabaseConnection.getConnection();
    }

    // Add a new driver
    public boolean addDriver(Driver driver) {
        String query = "INSERT INTO drivers (driver_name, driver_license, phone_number, driver_status) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, driver.getDriverName());
            stmt.setString(2, driver.getDriverLicense());
            stmt.setString(3, driver.getPhoneNumber());
            stmt.setString(4, driver.getDriverStatus());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Get all drivers
    public List<Driver> getAllDrivers() {
        String query = "SELECT * FROM drivers";
        List<Driver> drivers = new ArrayList<>();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                drivers.add(new Driver(
                        rs.getInt("driver_id"),
                        rs.getString("driver_name"),
                        rs.getString("driver_license"),
                        rs.getString("phone_number"),
                        rs.getString("driver_status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return drivers;
    }

    // Get available drivers
    public List<Driver> getAvailableDrivers() {
        String query = "SELECT * FROM drivers WHERE driver_status = 'Available'";
        List<Driver> drivers = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                drivers.add(new Driver(
                        rs.getInt("driver_id"),
                        rs.getString("driver_name"),
                        rs.getString("driver_license"),
                        rs.getString("phone_number"),
                        rs.getString("driver_status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return drivers;
    }

    // Get a driver by ID
    public Driver getDriverById(int driverId) {
        String query = "SELECT * FROM drivers WHERE driver_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, driverId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Driver(
                        rs.getInt("driver_id"),
                        rs.getString("driver_name"),
                        rs.getString("driver_license"),
                        rs.getString("phone_number"),
                        rs.getString("driver_status")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ✅ **Fix: Update driver details**
    public boolean updateDriver(Driver driver) {
        String query = "UPDATE drivers SET driver_name = ?, driver_license = ?, phone_number = ?, driver_status = ? WHERE driver_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, driver.getDriverName());
            stmt.setString(2, driver.getDriverLicense());
            stmt.setString(3, driver.getPhoneNumber());
            stmt.setString(4, driver.getDriverStatus());
            stmt.setInt(5, driver.getDriverId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ✅ **Fix: Delete a driver**
    public boolean deleteDriver(int driverId) {
        String query = "DELETE FROM drivers WHERE driver_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, driverId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
