package com.megacitycab.dao;

import com.megacitycab.models.Booking;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BookingDAO {
    private static final Logger logger = Logger.getLogger(BookingDAO.class.getName());
    private Connection conn;

    public BookingDAO() {
        conn = DatabaseConnection.getConnection();
        if (conn == null) {
            logger.severe("❌ Database connection is NULL! Check your DatabaseConnection class.");
        }
    }

    // ✅ Add a new booking & Retrieve Generated ID
    public boolean addBooking(Booking booking) {
        String query = "INSERT INTO bookings (customer_id, pickup_location, destination, car_type, date_time, distance_km, status, driver_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, booking.getCustomerId());
            stmt.setString(2, booking.getPickupLocation());
            stmt.setString(3, booking.getDestination());
            stmt.setString(4, booking.getCarType());
            stmt.setString(5, booking.getDateTime());
            stmt.setDouble(6, booking.getDistanceKm());
            stmt.setString(7, booking.getStatus());
            stmt.setObject(8, booking.getDriverId() > 0 ? booking.getDriverId() : null, Types.INTEGER);

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    booking.setBookingId(generatedKeys.getInt(1));
                    logger.info("✅ Booking added successfully with ID: " + booking.getBookingId());
                    return true;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "❌ Error in addBooking: ", e);
        }
        return false;
    }

    // ✅ Assign a driver when accepting a booking
    public boolean assignDriver(int bookingId, int driverId) {
        String query = "UPDATE bookings SET driver_id = ?, status = 'Accepted' WHERE booking_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, driverId);
            stmt.setInt(2, bookingId);

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                logger.info("✅ Booking ID " + bookingId + " assigned to Driver ID " + driverId);
                return true;
            } else {
                logger.warning("❌ Failed to assign Driver ID " + driverId + " to Booking ID " + bookingId);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "❌ Error in assignDriver: ", e);
        }
        return false;
    }

    // ✅ Get all bookings assigned to a driver
    public List<Booking> getAssignedBookings(int driverId) {
        List<Booking> bookings = new ArrayList<>();

        if (conn == null) {
            logger.severe("❌ Database connection is NULL! Cannot fetch assigned bookings.");
            return bookings;
        }

        String query = "SELECT booking_id, customer_id, pickup_location, destination, car_type, date_time, distance_km, total_fare, status, driver_id " +
                "FROM bookings WHERE driver_id = ? AND status IN ('Upcoming', 'Accepted', 'Pending')";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, driverId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                bookings.add(mapResultSetToBooking(rs));
            }

            if (bookings.isEmpty()) {
                logger.info("📭 No assigned bookings found for Driver ID: " + driverId);
            } else {
                logger.info("✅ Found " + bookings.size() + " assigned bookings for Driver ID: " + driverId);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "❌ Error in getAssignedBookings: ", e);
        }
        return bookings;
    }

    // ✅ Retrieve a single booking by ID
    public Booking getBookingById(int bookingId) {
        String query = "SELECT * FROM bookings WHERE booking_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, bookingId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToBooking(rs);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "❌ Error in getBookingById: ", e);
        }
        return null;
    }

    // ✅ Update Booking Status
    public boolean updateBookingStatus(int bookingId, String status) {
        String query = "UPDATE bookings SET status = ? WHERE booking_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, status);
            stmt.setInt(2, bookingId);

            boolean updated = stmt.executeUpdate() > 0;
            if (updated) {
                logger.info("✅ Booking ID " + bookingId + " updated to status: " + status);
            } else {
                logger.warning("❌ Failed to update status for Booking ID: " + bookingId);
            }
            return updated;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "❌ Error in updateBookingStatus: ", e);
        }
        return false;
    }

    // ✅ Get all bookings for a specific customer
    public List<Booking> getBookingsByCustomerId(int customerId) {
        List<Booking> bookings = new ArrayList<>();
        String query = "SELECT * FROM bookings WHERE customer_id = ? ORDER BY date_time DESC";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                bookings.add(mapResultSetToBooking(rs));
            }

            if (bookings.isEmpty()) {
                logger.info("📭 No bookings found for customer ID: " + customerId);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "❌ Error in getBookingsByCustomerId: ", e);
        }
        return bookings;
    }

    // ✅ Cancel a booking
    public boolean cancelBooking(int bookingId) {
        String updateQuery = "UPDATE bookings SET status = 'Canceled', driver_id = NULL WHERE booking_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
            stmt.setInt(1, bookingId);
            boolean updated = stmt.executeUpdate() > 0;
            if (updated) {
                logger.info("✅ Booking ID " + bookingId + " has been canceled.");
            } else {
                logger.warning("❌ Failed to cancel booking ID: " + bookingId);
            }
            return updated;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "❌ Error in cancelBooking: ", e);
        }
        return false;
    }

    // ✅ Store fare in database for completed bookings
    public boolean updateBookingFare(int bookingId, double totalFare) {
        String query = "UPDATE bookings SET total_fare = ? WHERE booking_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setDouble(1, totalFare);
            stmt.setInt(2, bookingId);
            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated > 0) {
                logger.info("✅ Fare updated for Booking ID " + bookingId);
                return true;
            } else {
                logger.warning("❌ Failed to update fare for Booking ID: " + bookingId);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "❌ Error in updateBookingFare: ", e);
        }
        return false;
    }

    // ✅ Helper method to map ResultSet to Booking object
    private Booking mapResultSetToBooking(ResultSet rs) throws SQLException {
        return new Booking(
                rs.getInt("booking_id"),
                rs.getInt("customer_id"),
                rs.getString("pickup_location"),
                rs.getString("destination"),
                rs.getString("car_type"),
                rs.getString("date_time"),
                rs.getDouble("distance_km"),
                rs.getDouble("total_fare"),
                rs.getString("status"),
                rs.getObject("driver_id") != null ? rs.getInt("driver_id") : -1
        );
    }
}