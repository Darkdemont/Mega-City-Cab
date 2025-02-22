package com.megacitycab.servlets;

import com.megacitycab.dao.DriverDAO;
import com.megacitycab.models.Driver;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/manageDrivers")
public class DriverServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(DriverServlet.class.getName());
    private DriverDAO driverDAO;

    @Override
    public void init() {
        driverDAO = new DriverDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            List<Driver> drivers = driverDAO.getAllDrivers();
            request.setAttribute("drivers", drivers);
            request.getRequestDispatcher("manageDrivers.jsp").forward(request, response);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "❌ Error fetching driver list", e);
            response.sendRedirect("manageDrivers.jsp?error=Unable to fetch drivers");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        try {
            if ("delete".equals(action)) {
                handleDeleteDriver(request, response);
            } else if ("update".equals(action)) {
                handleUpdateDriver(request, response);
            } else {
                handleAddDriver(request, response);
            }
        } catch (NumberFormatException e) {
            logger.log(Level.SEVERE, "❌ Invalid number format in request parameters", e);
            response.sendRedirect("manageDrivers?success=false&error=Invalid input format");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "❌ Error processing driver management request", e);
            response.sendRedirect("manageDrivers?success=false&error=Internal Server Error");
        }
    }


    private void handleDeleteDriver(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int driverId = Integer.parseInt(request.getParameter("driverId"));
        boolean deleted = driverDAO.deleteDriver(driverId);
        logger.info("🗑️ Driver Deletion Status: " + deleted);
        response.sendRedirect("manageDrivers?success=" + (deleted ? "true" : "false"));
    }


    private void handleUpdateDriver(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int driverId = Integer.parseInt(request.getParameter("driverId"));
        String driverName = request.getParameter("driverName");
        String driverLicense = request.getParameter("driverLicense");
        String phoneNumber = request.getParameter("phoneNumber");
        String driverStatus = request.getParameter("driverStatus");

        if (isNullOrEmpty(driverName, driverLicense, phoneNumber, driverStatus)) {
            logger.warning("❌ Missing parameters for driver update.");
            response.sendRedirect("manageDrivers?success=false&error=Missing required fields");
            return;
        }

        Driver driver = new Driver(driverId, driverName, driverLicense, phoneNumber, driverStatus);
        boolean updated = driverDAO.updateDriver(driver);
        logger.info("✏️ Driver Update Status: " + updated);
        response.sendRedirect("manageDrivers?success=" + (updated ? "true" : "false"));
    }


    private void handleAddDriver(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String driverName = request.getParameter("driverName");
        String driverLicense = request.getParameter("driverLicense");
        String phoneNumber = request.getParameter("phoneNumber");
        String driverStatus = request.getParameter("driverStatus");
        String username = request.getParameter("username");
        String password = request.getParameter("password");


        if (isNullOrEmpty(username)) {
            username = generateUsername(driverName);
        }
        if (isNullOrEmpty(password)) {
            password = driverLicense;
        }

        if (isNullOrEmpty(driverName, driverLicense, phoneNumber, driverStatus, username, password)) {
            logger.warning("❌ Missing parameters for driver addition.");
            response.sendRedirect("manageDrivers?success=false&error=Missing required fields");
            return;
        }

        Driver driver = new Driver(driverName, driverLicense, phoneNumber, driverStatus, username, password);
        boolean added = driverDAO.addDriver(driver);

        logger.info("✅ Driver Addition Status: " + added);

        if (added) {
            // ✅ Encode username and password to prevent URL issues
            String encodedUsername = URLEncoder.encode(username, StandardCharsets.UTF_8);
            String encodedPassword = URLEncoder.encode(password, StandardCharsets.UTF_8);
            response.sendRedirect("manageDrivers?success=true&username=" + encodedUsername + "&password=" + encodedPassword);
        } else {
            response.sendRedirect("manageDrivers?success=false");
        }
    }


    private boolean isNullOrEmpty(String... values) {
        for (String value : values) {
            if (value == null || value.trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }


    private String generateUsername(String driverName) {
        return driverName.replaceAll("\\s+", "").toLowerCase() + System.currentTimeMillis() % 10000; // Append timestamp to avoid duplicates
    }
}