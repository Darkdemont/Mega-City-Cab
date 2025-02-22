package com.megacitycab.dao;

import com.megacitycab.models.Driver;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DriverDAO {
    private static final Logger logger = Logger.getLogger(DriverDAO.class.getName());
    private Connection conn;

    public DriverDAO() {
        conn = DatabaseConnection.getConnection();
    }

    // ✅ Add a new driver & automatically create login credentials
    public boolean addDriver(Driver driver) {
        String userQuery = "INSERT INTO users (username, email, password, role) VALUES (?, ?, ?, ?)";
        String driverQuery = "INSERT INTO drivers (driver_name, driver_license, phone_number, driver_status, user_id) VALUES (?, ?, ?, ?, ?)";

        try {
            conn.setAutoCommit(false); // ✅ Begin transaction

            // ✅ Generate unique username if missing
            String username = driver.getUsername();
            if (username == null || username.isEmpty()) {
                username = generateUsername(driver.getDriverName());
            }

            // ✅ Default password is driver license if missing
            String password = driver.getPassword();
            if (password == null || password.isEmpty()) {
                password = driver.getDriverLicense();
            }

            // ✅ Hash the password before storing
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));

            // ✅ Insert into `users` table
            try (PreparedStatement userStmt = conn.prepareStatement(userQuery, Statement.RETURN_GENERATED_KEYS)) {
                userStmt.setString(1, username);
                userStmt.setString(2, driver.getPhoneNumber() + "@megacitycab.com");
                userStmt.setString(3, hashedPassword);
                userStmt.setString(4, "Driver");
                userStmt.executeUpdate();

                ResultSet generatedKeys = userStmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int userId = generatedKeys.getInt(1);

                    // ✅ Insert into `drivers` table with `user_id`
                    try (PreparedStatement driverStmt = conn.prepareStatement(driverQuery)) {
                        driverStmt.setString(1, driver.getDriverName());
                        driverStmt.setString(2, driver.getDriverLicense());
                        driverStmt.setString(3, driver.getPhoneNumber());
                        driverStmt.setString(4, driver.getDriverStatus());
                        driverStmt.setInt(5, userId);
                        driverStmt.executeUpdate();
                    }

                    conn.commit();
                    driver.setUsername(username);
                    driver.setPassword(password); // Store plaintext for response
                    return true;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "❌ Error in addDriver: ", e);
            try { conn.rollback(); } catch (SQLException rollbackEx) { rollbackEx.printStackTrace(); }
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException ignored) {}
        }
        return false;
    }

    // ✅ Get driver ID by User ID (Fix for LoginServlet)
    public int getDriverIdByUserId(int userId) {
        String query = "SELECT driver_id FROM drivers WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("driver_id"); // ✅ Return correct driver_id
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "❌ Error in getDriverIdByUserId: ", e);
        }
        return -1; // ✅ Return invalid ID if not found
    }

    // ✅ Get all drivers
    public List<Driver> getAllDrivers() {
        String query = "SELECT d.*, u.username FROM drivers d JOIN users u ON d.user_id = u.user_id ORDER BY d.driver_name ASC";
        List<Driver> drivers = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                drivers.add(mapResultSetToDriver(rs));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "❌ Error in getAllDrivers: ", e);
        }
        return drivers;
    }

    // ✅ Get available drivers
    public List<Driver> getAvailableDrivers() {
        String query = "SELECT d.*, u.username FROM drivers d JOIN users u ON d.user_id = u.user_id WHERE d.driver_status = 'Available'";
        List<Driver> drivers = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                drivers.add(mapResultSetToDriver(rs));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "❌ Error in getAvailableDrivers: ", e);
        }
        return drivers;
    }

    // ✅ Mark driver as "On Duty"
    public boolean markDriverOnDuty(int driverId) {
        return updateDriverStatus(driverId, "On Duty");
    }

    // ✅ Mark driver as "Available"
    public boolean markDriverAvailable(int driverId) {
        return updateDriverStatus(driverId, "Available");
    }

    // ✅ Generic method to update driver status
    private boolean updateDriverStatus(int driverId, String status) {
        String query = "UPDATE drivers SET driver_status = ? WHERE driver_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, status);
            stmt.setInt(2, driverId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "❌ Error updating driver status: ", e);
        }
        return false;
    }

    // ✅ Update driver details
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
            logger.log(Level.SEVERE, "❌ Error in updateDriver: ", e);
        }
        return false;
    }

    // ✅ Delete a driver
    public boolean deleteDriver(int driverId) {
        String deleteDriverQuery = "DELETE FROM drivers WHERE driver_id = ?";
        String deleteUserQuery = "DELETE FROM users WHERE user_id = (SELECT user_id FROM drivers WHERE driver_id = ?)";

        try {
            conn.setAutoCommit(false);
            try (PreparedStatement deleteUserStmt = conn.prepareStatement(deleteUserQuery)) {
                deleteUserStmt.setInt(1, driverId);
                deleteUserStmt.executeUpdate();
            }
            try (PreparedStatement deleteDriverStmt = conn.prepareStatement(deleteDriverQuery)) {
                deleteDriverStmt.setInt(1, driverId);
                deleteDriverStmt.executeUpdate();
            }
            conn.commit();
            return true;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "❌ Error in deleteDriver: ", e);
            try { conn.rollback(); } catch (SQLException rollbackEx) { rollbackEx.printStackTrace(); }
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException ignored) {}
        }
        return false;
    }

    // ✅ Generate a unique username based on the driver name
    private String generateUsername(String driverName) {
        return driverName.replaceAll("\\s+", "").toLowerCase() + System.currentTimeMillis() % 10000;
    }

    // ✅ Map database result to Driver object
    private Driver mapResultSetToDriver(ResultSet rs) throws SQLException {
        return new Driver(
                rs.getInt("driver_id"),
                rs.getString("driver_name"),
                rs.getString("driver_license"),
                rs.getString("phone_number"),
                rs.getString("driver_status"),
                rs.getString("username"),
                "****" // Mask password
        );
    }
}
