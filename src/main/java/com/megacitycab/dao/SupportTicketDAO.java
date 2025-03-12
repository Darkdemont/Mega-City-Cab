package com.megacitycab.dao;

import com.megacitycab.models.SupportTicket;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SupportTicketDAO {
    private static final Logger logger = Logger.getLogger(SupportTicketDAO.class.getName());
    private Connection conn;

    public SupportTicketDAO() {
        conn = DatabaseConnection.getConnection();
    }


    public boolean createSupportTicket(int userId, String subject, String description) {
        String query = "INSERT INTO support_tickets (user_id, subject, description, status, created_at) VALUES (?, ?, ?, 'Open', NOW())";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setString(2, subject);
            stmt.setString(3, description);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "❌ Error submitting support ticket:", e);
        }
        return false;
    }


    public List<SupportTicket> getAllSupportTickets() {
        List<SupportTicket> tickets = new ArrayList<>();
        String query = "SELECT * FROM support_tickets ORDER BY created_at DESC";
        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                tickets.add(mapResultSetToTicket(rs));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "❌ Error fetching support tickets:", e);
        }
        return tickets;
    }


    public List<SupportTicket> getTicketsByUserId(int userId) {
        List<SupportTicket> tickets = new ArrayList<>();
        String query = "SELECT * FROM support_tickets WHERE user_id = ? ORDER BY created_at DESC";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tickets.add(mapResultSetToTicket(rs));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "❌ Error fetching user support tickets:", e);
        }
        return tickets;
    }


    public boolean updateTicketStatus(int ticketId, String status) {
        String query = "UPDATE support_tickets SET status = ? WHERE ticket_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, status);
            stmt.setInt(2, ticketId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "❌ Error updating ticket status:", e);
        }
        return false;
    }


    public SupportTicket getTicketById(int ticketId) {
        String query = "SELECT * FROM support_tickets WHERE ticket_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, ticketId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToTicket(rs);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "❌ Error fetching ticket by ID:", e);
        }
        return null;
    }


    private SupportTicket mapResultSetToTicket(ResultSet rs) throws SQLException {
        return new SupportTicket(
                rs.getInt("ticket_id"),
                rs.getInt("user_id"),
                rs.getString("subject"),
                rs.getString("description"),
                rs.getString("status"),
                rs.getTimestamp("created_at")
        );
    }
}
