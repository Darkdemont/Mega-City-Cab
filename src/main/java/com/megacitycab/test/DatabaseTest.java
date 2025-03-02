package com.megacitycab.test;

import com.megacitycab.dao.DatabaseConnection;
import java.sql.Connection;

public class DatabaseTest {
    public static void main(String[] args) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn != null) {
            System.out.println("✅ Connection Test Successful!");
        } else {
            System.out.println("❌ Connection Test Failed!");
        }
    }
}
