package com.megacitycab.servlets;

import com.megacitycab.dao.BookingDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/DriverActionServlet")
public class DriverActionServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(DriverActionServlet.class.getName());
    private BookingDAO bookingDAO = new BookingDAO(); // ✅ Ensure BookingDAO is initialized

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        // ✅ Ensure the driver is logged in
        if (session == null || session.getAttribute("driverId") == null) {
            logger.severe("❌ Driver session expired or missing!");
            response.sendRedirect("login.jsp?error=SessionExpired");
            return;
        }

        int driverId = (Integer) session.getAttribute("driverId");
        String action = request.getParameter("action");
        String bookingIdStr = request.getParameter("bookingId");

        // ✅ Check if bookingId is missing or invalid
        if (bookingIdStr == null || bookingIdStr.isEmpty()) {
            logger.severe("❌ Missing bookingId in request!");
            response.sendRedirect("driverIndex.jsp?error=InvalidBooking");
            return;
        }

        int bookingId;
        try {
            bookingId = Integer.parseInt(bookingIdStr);
        } catch (NumberFormatException e) {
            logger.log(Level.SEVERE, "❌ Invalid bookingId format: " + bookingIdStr, e);
            response.sendRedirect("driverIndex.jsp?error=InvalidBooking");
            return;
        }

        boolean success = false;

        switch (action) {
            case "accept":
                success = bookingDAO.assignDriver(bookingId, driverId); // ✅ Assign driver when accepted
                if (success) {
                    logger.info("✅ Booking " + bookingId + " assigned to Driver " + driverId);
                } else {
                    logger.warning("❌ Failed to assign booking " + bookingId + " to Driver " + driverId);
                }
                break;
            case "reject":
                success = bookingDAO.updateBookingStatus(bookingId, "Rejected");
                break;
            case "complete":
                success = bookingDAO.updateBookingStatus(bookingId, "Completed");
                break;
            default:
                logger.severe("❌ Invalid action: " + action);
                response.sendRedirect("driverIndex.jsp?error=InvalidAction");
                return;
        }

        if (!success) {
            logger.severe("❌ Failed to update booking status for bookingId: " + bookingId);
            response.sendRedirect("driverIndex.jsp?error=UpdateFailed");
        } else {
            logger.info("✅ Booking status updated successfully. BookingId: " + bookingId + ", Action: " + action);
            response.sendRedirect("DriverDashboardServlet"); // Reload dashboard
        }
    }
}