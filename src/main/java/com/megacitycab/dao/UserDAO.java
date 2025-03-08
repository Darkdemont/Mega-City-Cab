package com.megacitycab.dao;

import com.megacitycab.models.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mindrot.jbcrypt.BCrypt;

public class UserDAO {
    private static final Logger logger = Logger.getLogger(UserDAO.class.getName());
    private Connection conn;

    public UserDAO() {
        conn = DatabaseConnection.getConnection();
    }

    // ✅ Check if a username or email already exists
    public boolean isUserExists(String username, String email) {
        String query = "SELECT user_id FROM users WHERE username = ? OR email = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, email);
            ResultSet rs = stmt.executeQuery();
            return rs.next(); // If a row exists, the user exists
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "❌ SQLException in isUserExists:", e);
        }
        return false;
    }

    // ✅ Insert new user into the database (with hashed password)
    public boolean createUser(User user) {
        String query = "INSERT INTO users (username, email, password, role) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword()); // Hashed password
            stmt.setString(4, user.getRole());

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    user.setUserId(generatedKeys.getInt(1)); // ✅ Set generated user ID
                }
                return true;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "❌ SQLException in createUser:", e);
        }
        return false;
    }

    // ✅ Fetch User Data by Username (Includes Role)
    public User getUserByUsername(String username) {
        String query = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getString("phone"),
                        rs.getString("nic"),
                        rs.getString("address")
                );
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "❌ SQLException in getUserByUsername:", e);
        }
        return null;
    }

    // ✅ Fetch User by ID
    public User getUserById(int userId) {
        String query = "SELECT * FROM users WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getString("phone"),
                        rs.getString("nic"),
                        rs.getString("address")

                );
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "❌ SQLException in getUserById:", e);
        }
        return null;
    }

    // ✅ Validate user login (Checks username & password securely)
    public boolean validateUser(String username, String password) {
        String query = "SELECT password FROM users WHERE username = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                return BCrypt.checkpw(password, storedPassword); // Compare hashed password
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "❌ SQLException in validateUser:", e);
        }
        return false;
    }

    // ✅ Fetch all users (For Admin)
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM users";
        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "❌ Error fetching all users:", e);
        }
        return users;
    }

    // ✅ Get total customers count
    public int getTotalCustomers() {
        String query = "SELECT COUNT(*) FROM users WHERE role = 'Customer'";
        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "❌ Error fetching total customers:", e);
        }
        return 0;
    }

    // ✅ Reset user password
    public boolean resetPassword(int userId, String newPassword) {
        String query = "UPDATE users SET password = ? WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, newPassword); // Hash before saving in production
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "❌ Error resetting password:", e);
        }
        return false;
    }

    // ✅ Delete user
    public boolean deleteUser(int userId) {
        String query = "DELETE FROM users WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "❌ Error deleting user:", e);
        }
        return false;
    }

    // ✅ Update Customer Profile (Address, NIC, phone)
    public boolean updateCustomerDetails(int userId, String phone, String nic, String address) {
        String query = "UPDATE users SET phone = ?, nic = ?, address = ? WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, phone);
            stmt.setString(2, nic);
            stmt.setString(3, address);
            stmt.setInt(4, userId);

            int rowsUpdated = stmt.executeUpdate();
            System.out.println("📌 SQL Executed - Rows Updated: " + rowsUpdated); // Debug log
            return rowsUpdated > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "❌ Error updating customer details:", e);
        }
        return false;
    }





    // ✅ Helper method to map ResultSet to User object
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("user_id"),
                rs.getString("username"),
                rs.getString("email"),
                rs.getString("password"),
                rs.getString("role"),
                rs.getString("phone") != null ? rs.getString("phone") : "",  // Prevent null issues
                rs.getString("nic") != null ? rs.getString("nic") : "",
                rs.getString("address") != null ? rs.getString("address") : ""

        );
    }
}
