package com.megacitycab.dao;

import com.megacitycab.models.Payment;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BillingDAO {
    private static final Logger logger = Logger.getLogger(BillingDAO.class.getName());
    private Connection conn;

    public BillingDAO(Connection conn) {
        this.conn = conn;
    }


    public boolean savePayment(Payment payment) {
        String sql = "INSERT INTO payments (booking_id, customer_id, amount_paid, payment_method, payment_status) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, payment.getBookingId());
            stmt.setInt(2, payment.getCustomerId());
            stmt.setBigDecimal(3, payment.getAmountPaid());
            stmt.setString(4, payment.getPaymentMethod());
            stmt.setString(5, payment.getPaymentStatus());

            boolean isInserted = stmt.executeUpdate() > 0;
            if (isInserted) {
                logger.info("✅ Payment saved successfully for Booking ID: " + payment.getBookingId());
            } else {
                logger.warning("⚠️ Failed to save payment for Booking ID: " + payment.getBookingId());
            }
            return isInserted;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "❌ Error inserting payment record: ", e);
            return false;
        }
    }


    public boolean updatePaymentStatus(int bookingId, String status) {
        String sql = "UPDATE payments SET payment_status = ? WHERE booking_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, bookingId);

            boolean isUpdated = stmt.executeUpdate() > 0;
            if (isUpdated) {
                logger.info("✅ Payment status updated to '" + status + "' for Booking ID: " + bookingId);
            } else {
                logger.warning("⚠️ Failed to update payment status for Booking ID: " + bookingId);
            }
            return isUpdated;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "❌ Error updating payment status: ", e);
            return false;
        }
    }


    public Payment getPaymentByBookingId(int bookingId) {
        String sql = "SELECT * FROM payments WHERE booking_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookingId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Payment payment = new Payment(
                        rs.getInt("booking_id"),
                        rs.getInt("customer_id"),
                        rs.getBigDecimal("amount_paid"),
                        rs.getString("payment_method"),
                        rs.getString("payment_status")
                );
                logger.info("✅ Payment details retrieved for Booking ID: " + bookingId);
                return payment;
            } else {
                logger.warning("⚠️ No payment record found for Booking ID: " + bookingId);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "❌ Error retrieving payment details: ", e);
        }
        return null;
    }
}
