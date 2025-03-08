package com.megacitycab.dao;

import com.megacitycab.models.User;
import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;

public class UserDAO {
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
            System.out.println("❌ SQLException in isUserExists:");
            e.printStackTrace();
            return false;
        }
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
            System.out.println("❌ SQLException in createUser:");
            e.printStackTrace();
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
                        rs.getInt("user_id"), // ✅ Using `user_id` to match database column
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("role")
                );
            }
        } catch (SQLException e) {
            System.out.println("❌ SQLException in getUserByUsername:");
            e.printStackTrace();
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
                        rs.getInt("user_id"), // ✅ Using `user_id` to match database column
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("role")
                );
            }
        } catch (SQLException e) {
            System.out.println("❌ SQLException in getUserById:");
            e.printStackTrace();
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
            System.out.println("❌ SQLException in validateUser:");
            e.printStackTrace();
        }
        return false;
    }
}
