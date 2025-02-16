package com.megacitycab.servlets;

import com.megacitycab.dao.DriverDAO;
import com.megacitycab.models.Driver;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/manageDrivers")
public class DriverServlet extends HttpServlet {
    private DriverDAO driverDAO;

    @Override
    public void init() {
        driverDAO = new DriverDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Driver> drivers = driverDAO.getAllDrivers();
        request.setAttribute("drivers", drivers);
        request.getRequestDispatcher("manageDrivers.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("delete".equals(action)) {
            int driverId = Integer.parseInt(request.getParameter("driverId"));
            boolean deleted = driverDAO.deleteDriver(driverId);
            System.out.println("Driver Deletion Status: " + deleted);
        } else if (request.getParameter("driverId") != null && !request.getParameter("driverId").isEmpty()) {
            // Update driver details
            int driverId = Integer.parseInt(request.getParameter("driverId"));
            String driverName = request.getParameter("driverName");
            String driverLicense = request.getParameter("driverLicense");
            String phoneNumber = request.getParameter("phoneNumber");
            String driverStatus = request.getParameter("driverStatus");

            Driver driver = new Driver(driverId, driverName, driverLicense, phoneNumber, driverStatus);
            boolean updated = driverDAO.updateDriver(driver);
            System.out.println("Driver Update Status: " + updated);
        } else {
            // Add new driver
            String driverName = request.getParameter("driverName");
            String driverLicense = request.getParameter("driverLicense");
            String phoneNumber = request.getParameter("phoneNumber");
            String driverStatus = request.getParameter("driverStatus");

            Driver driver = new Driver(driverName, driverLicense, phoneNumber, driverStatus);
            boolean added = driverDAO.addDriver(driver);
            System.out.println("Driver Addition Status: " + added);
        }

        response.sendRedirect("manageDrivers");
    }
}
