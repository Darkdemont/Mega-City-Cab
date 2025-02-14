package com.megacitycab.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/MegaCityCabDB";
    private static final String USER = "root"; // Change to your DB username
    private static final String PASSWORD = "23331"; // Change to your DB password
    private static Connection connection;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✅ Database connected successfully!");
            } catch (ClassNotFoundException | SQLException e) {
                System.out.println("❌ Database connection failed!");
                e.printStackTrace();
            }
        }
        return connection;
    }
}
