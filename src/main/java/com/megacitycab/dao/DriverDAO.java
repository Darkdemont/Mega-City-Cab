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
    private final Connection conn;

    public DriverDAO() {
        conn = DatabaseConnection.getConnection();
        if (conn == null) {
            logger.severe("❌ Database connection is NULL! Check your DatabaseConnection class.");
        }
    }


    public boolean addDriver(Driver driver) {
        String userQuery = "INSERT INTO users (username, email, password, role) VALUES (?, ?, ?, ?)";
        String driverQuery = "INSERT INTO drivers (driver_name, driver_license, phone_number, driver_status, user_id) VALUES (?, ?, ?, ?, ?)";

        try {
            conn.setAutoCommit(false); // ✅ Start transaction


            String username = (driver.getUsername() == null || driver.getUsername().isEmpty())
                    ? generateUsername(driver.getDriverName())
                    : driver.getUsername();


            String password = (driver.getPassword() == null || driver.getPassword().isEmpty())
                    ? driver.getDriverLicense()
                    : driver.getPassword();


            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));


            try (PreparedStatement userStmt = conn.prepareStatement(userQuery, Statement.RETURN_GENERATED_KEYS)) {
                userStmt.setString(1, username);
                userStmt.setString(2, driver.getPhoneNumber() + "@megacitycab.com");
                userStmt.setString(3, hashedPassword);
                userStmt.setString(4, "Driver");
                userStmt.executeUpdate();

                ResultSet generatedKeys = userStmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int userId = generatedKeys.getInt(1);


                    try (PreparedStatement driverStmt = conn.prepareStatement(driverQuery)) {
                        driverStmt.setString(1, driver.getDriverName());
                        driverStmt.setString(2, driver.getDriverLicense());
                        driverStmt.setString(3, driver.getPhoneNumber());
                        driverStmt.setString(4, "Available");
                        driverStmt.setInt(5, userId);
                        driverStmt.executeUpdate();
                    }

                    conn.commit();
                    driver.setUsername(username);
                    driver.setPassword(password);
                    return true;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "❌ Error in addDriver: ", e);
            try {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException ignored) {}
        }
        return false;
    }


    public int getDriverIdByUserId(int userId) {
        String query = "SELECT driver_id FROM drivers WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("driver_id");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "❌ Error in getDriverIdByUserId: ", e);
        }
        return -1;
    }


    public int getAvailableDriver() {
        String query = "SELECT driver_id FROM drivers WHERE driver_status = 'Available' LIMIT 1";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("driver_id");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "❌ Error in getAvailableDriver: ", e);
        }
        return -1;
    }


    public List<Driver> getAvailableDrivers() {
        List<Driver> drivers = new ArrayList<>();
        String query = "SELECT * FROM drivers WHERE driver_status = 'Available'"; // ✅ Ensure "Available" is the correct status

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
            logger.log(Level.SEVERE, "❌ Error fetching available drivers", e);
        }
        return drivers;
    }

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
            logger.log(Level.SEVERE, "❌ Error fetching driver by ID", e);
        }
        return null;
    }





    public List<Driver> getAllDrivers() {
        List<Driver> drivers = new ArrayList<>();
        String query = "SELECT d.driver_id, d.driver_name, d.driver_license, d.phone_number, d.driver_status, u.username " +
                "FROM drivers d JOIN users u ON d.user_id = u.user_id ORDER BY d.driver_name ASC";

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


    public boolean markDriverOnDuty(int driverId) {
        return updateDriverStatus(driverId, "On Duty");
    }


    public boolean markDriverAvailable(int driverId) {
        return updateDriverStatus(driverId, "Available");
    }


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


    public boolean deleteDriver(int driverId) {
        String query = "DELETE FROM drivers WHERE driver_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, driverId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "❌ Error in deleteDriver: ", e);
        }
        return false;
    }

    public Driver getDriverByBookingId(int bookingId) {
        String query = "SELECT d.* FROM drivers d INNER JOIN bookings b ON d.driver_id = b.driver_id WHERE b.booking_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, bookingId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Driver(
                        rs.getInt("driver_id"),
                        rs.getString("driver_name"),
                        rs.getString("driver_license"),
                        rs.getString("phone_number"),
                        rs.getString("driver_status"),
                        rs.getInt("user_id")
                );
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "❌ Error fetching driver by booking ID", e);
        }
        return null; // If no driver is found, return null
    }





    private String generateUsername(String driverName) {
        return driverName.replaceAll("\\s+", "").toLowerCase() + System.currentTimeMillis() % 10000;
    }


    private Driver mapResultSetToDriver(ResultSet rs) throws SQLException {
        return new Driver(
                rs.getInt("driver_id"),
                rs.getString("driver_name"),
                rs.getString("driver_license"),
                rs.getString("phone_number"),
                rs.getString("driver_status"),
                rs.getInt("user_id")

        );
    }
}
