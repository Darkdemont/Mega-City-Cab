package com.megacitycab.servlets;

import com.megacitycab.dao.BookingDAO;
import com.megacitycab.models.Booking;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/DriverDashboardServlet")
public class DriverDashboardServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(DriverDashboardServlet.class.getName());
    private BookingDAO bookingDAO;

    @Override
    public void init() {
        bookingDAO = new BookingDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false); // ✅ Prevent new session creation

        if (session == null || session.getAttribute("loggedInUser") == null) {
            logger.warning("❌ No active session found! Redirecting to login.");
            response.sendRedirect("login.jsp?error=SessionExpired");
            return;
        }

        Integer driverId = (Integer) session.getAttribute("driverId");
        if (driverId == null || driverId <= 0) {
            logger.warning("❌ Driver ID is missing or invalid in session! Redirecting to login.");
            response.sendRedirect("login.jsp?error=SessionExpired");
            return;
        }

        logger.info("🚗 Fetching bookings for Driver ID: " + driverId);

        // ✅ Retrieve assigned bookings for the driver
        List<Booking> assignedBookings = bookingDAO.getAssignedBookings(driverId);

        if (assignedBookings.isEmpty()) {
            logger.info("📭 No assigned bookings found for Driver ID: " + driverId);
        } else {
            logger.info("📦 Loaded " + assignedBookings.size() + " bookings for Driver ID: " + driverId);
        }

        request.setAttribute("bookings", assignedBookings);

        // ✅ Forward to driver dashboard
        request.getRequestDispatcher("driverIndex.jsp").forward(request, response);
    }
}