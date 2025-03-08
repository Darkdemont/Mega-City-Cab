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
    private DriverDAO driverDAO;

    public BookingDAO() {
        conn = DatabaseConnection.getConnection();
        driverDAO = new DriverDAO();
        if (conn == null) {
            logger.severe("❌ Database connection is NULL! Check your DatabaseConnection class.");
        }
    }


    public boolean updateBookingStatus(int bookingId, String status) {
        String query = "UPDATE bookings SET status = ? WHERE booking_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, status);
            stmt.setInt(2, bookingId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "❌ Error in updateBookingStatus: ", e);
        }
        return false;
    }


    public boolean addBooking(Booking booking) {
        String query = "INSERT INTO bookings (customer_id, pickup_location, destination, car_type, date_time, distance_km, status, total_fare, driver_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, NULL)";
        try (PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, booking.getCustomerId());
            stmt.setString(2, booking.getPickupLocation());
            stmt.setString(3, booking.getDestination());
            stmt.setString(4, booking.getCarType());
            stmt.setString(5, booking.getDateTime());
            stmt.setDouble(6, booking.getDistanceKm());
            stmt.setString(7, "Upcoming");
            stmt.setDouble(8, 0.0);

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


    public List<Booking> getBookingsByCustomerId(int customerId) {
        List<Booking> bookings = new ArrayList<>();
        String query = "SELECT * FROM bookings WHERE customer_id = ? ORDER BY date_time DESC";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                bookings.add(mapResultSetToBooking(rs));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "❌ Error in getBookingsByCustomerId: ", e);
        }
        return bookings;
    }


    public List<Booking> getUnassignedBookings() {
        List<Booking> bookings = new ArrayList<>();
        String query = "SELECT * FROM bookings WHERE driver_id IS NULL AND status = 'Upcoming'";
        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                bookings.add(mapResultSetToBooking(rs));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "❌ Error in getUnassignedBookings: ", e);
        }
        return bookings;
    }


    public List<Booking> getAssignedBookings(int driverId) {
        List<Booking> bookings = new ArrayList<>();
        String query = "SELECT * FROM bookings WHERE driver_id = ? AND status IN ('Accepted', 'Upcoming')";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, driverId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                bookings.add(mapResultSetToBooking(rs));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "❌ Error in getAssignedBookings: ", e);
        }
        return bookings;
    }


    public boolean assignDriver(int bookingId, int driverId, String status) {
        String query = "UPDATE bookings SET driver_id = ?, status = ? WHERE booking_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, driverId);
            stmt.setString(2, status);
            stmt.setInt(3, bookingId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "❌ Error in assignDriver: ", e);
        }
        return false;
    }


    public boolean unassignDriver(int bookingId) {
        String query = "UPDATE bookings SET driver_id = NULL, status = 'Upcoming' WHERE booking_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, bookingId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "❌ Error in unassignDriver: ", e);
        }
        return false;
    }


    public boolean cancelBooking(int bookingId) {
        String query = "UPDATE bookings SET status = 'Cancelled', driver_id = NULL WHERE booking_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, bookingId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "❌ Error in cancelBooking: ", e);
        }
        return false;
    }






    public int getCustomerRideCount(int customerId) {
        int rideCount = 0;
        String sql = "SELECT COUNT(*) FROM bookings WHERE customer_id = ? AND status = 'Completed'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                rideCount = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rideCount;
    }















    // ✅ Update booking fare
    public boolean updateBookingFare(int bookingId, double totalFare) {
        String query = "UPDATE bookings SET total_fare = ? WHERE booking_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setDouble(1, totalFare);
            stmt.setInt(2, bookingId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "❌ Error in updateBookingFare: ", e);
        }
        return false;
    }

    // ✅ Fetch a single booking by ID
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

    // ✅ Fetch all bookings (Admin Dashboard)
    public List<Booking> getAllBookings() {
        List<Booking> bookings = new ArrayList<>();
        String query = "SELECT * FROM bookings ORDER BY date_time DESC";

        System.out.println("📌 Executing getAllBookings() query...");

        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Booking booking = mapResultSetToBooking(rs);
                bookings.add(booking);
                System.out.println("✅ Loaded Booking ID: " + booking.getBookingId() + " | Customer: " + booking.getCustomerId());
            }

        } catch (SQLException e) {
            System.out.println("❌ Error fetching bookings: " + e.getMessage());
        }

        System.out.println("📌 Total bookings retrieved: " + bookings.size());
        return bookings;
    }

    // ✅ Get total earnings from completed bookings
    public double getTotalEarnings() {
        String query = "SELECT SUM(total_fare) FROM bookings WHERE status = 'Completed'";
        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "❌ Error fetching total earnings: ", e);
        }
        return 0.0;
    }

    // ✅ Get total completed bookings count
    public int getTotalBookings() {
        String query = "SELECT COUNT(*) FROM bookings WHERE status = 'Completed'";
        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "❌ Error fetching total bookings: ", e);
        }
        return 0;
    }

    public int getLastInsertedBookingId(int customerId) {
        String query = "SELECT booking_id FROM bookings WHERE customer_id = ? ORDER BY booking_id DESC LIMIT 1";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("booking_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Return -1 if no booking found
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