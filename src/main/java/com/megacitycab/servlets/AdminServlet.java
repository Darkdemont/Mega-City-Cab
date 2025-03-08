package com.megacitycab.servlets;

import com.megacitycab.dao.BookingDAO;
import com.megacitycab.dao.UserDAO;
import com.megacitycab.models.Booking;
import com.megacitycab.models.User;
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

@WebServlet("/AdminServlet")
public class AdminServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(AdminServlet.class.getName());

    private BookingDAO bookingDAO;
    private UserDAO userDAO;

    @Override
    public void init() {
        bookingDAO = new BookingDAO();
        userDAO = new UserDAO();
    }



    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("loggedInUser") == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        User admin = (User) session.getAttribute("loggedInUser");
        if (!"Admin".equals(admin.getRole())) {
            response.sendRedirect("index.jsp");
            return;
        }

        String action = request.getParameter("action");
        if (action == null || action.isEmpty()) {
            // Default action: Load Manage Bookings
            response.sendRedirect("AdminServlet?action=manageBookings");
            return;
        }

        System.out.println("📌 Admin requested action: " + action); // Debug Log

        switch (action) {
            case "manageBookings":
                handleManageBookings(request, response);
                break;
            case "manageUsers":
                handleManageUsers(request, response);
                break;
            case "generateReports":
                handleGenerateReports(request, response);
                break;
            default:
                response.sendRedirect("AdminServlet?action=manageBookings");
                break;
        }
    }


    private void handleManageBookings(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("📌 handleManageBookings() called"); // Debug log

        List<Booking> bookings = bookingDAO.getAllBookings();

        if (bookings == null) {
            System.out.println("❌ Booking list is NULL! BookingDAO may be failing.");
        } else {
            System.out.println("✅ Retrieved " + bookings.size() + " bookings.");
        }

        request.setAttribute("bookings", bookings);
        request.getRequestDispatcher("adminDashboard.jsp").forward(request, response);
    }






    private void handleManageUsers(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("📌 handleManageUsers() called"); // Debug Log

        List<User> users = userDAO.getAllUsers();
        if (users == null) {
            System.out.println("❌ User list is NULL! UserDAO may be failing.");
        } else {
            System.out.println("✅ Retrieved " + users.size() + " users.");
        }

        request.setAttribute("users", users);
        request.getRequestDispatcher("adminDashboard.jsp").forward(request, response);
    }


    private void handleGenerateReports(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("📌 handleGenerateReports() called"); // Debug Log

        double totalEarnings = bookingDAO.getTotalEarnings();
        int totalBookings = bookingDAO.getTotalBookings();
        int totalCustomers = userDAO.getTotalCustomers();

        System.out.println("📊 Total Earnings: " + totalEarnings);
        System.out.println("📊 Total Bookings: " + totalBookings);
        System.out.println("📊 Total Customers: " + totalCustomers);

        request.setAttribute("totalEarnings", totalEarnings);
        request.setAttribute("totalBookings", totalBookings);
        request.setAttribute("totalCustomers", totalCustomers);
        request.getRequestDispatcher("adminDashboard.jsp").forward(request, response);
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        logger.info("📌 Admin submitted POST action: " + action);

        switch (action) {
            case "cancelBooking":
                handleCancelBooking(request, response);
                break;
            case "resetPassword":
                handleResetPassword(request, response);
                break;
            case "deleteUser":
                handleDeleteUser(request, response);
                break;
            default:
                response.sendRedirect("AdminServlet?action=manageBookings");
                break;
        }
    }

    private void handleCancelBooking(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int bookingId = Integer.parseInt(request.getParameter("bookingId"));
            boolean success = bookingDAO.cancelBooking(bookingId);
            if (success) {
                logger.info("✅ Booking ID " + bookingId + " canceled successfully.");
                request.getSession().setAttribute("message", "Booking canceled successfully.");
            } else {
                logger.warning("❌ Failed to cancel booking ID: " + bookingId);
                request.getSession().setAttribute("errorMessage", "Failed to cancel booking.");
            }
        } catch (NumberFormatException e) {
            logger.log(Level.SEVERE, "❌ Invalid booking ID format", e);
        }
        response.sendRedirect("AdminServlet?action=manageBookings");
    }

    private void handleResetPassword(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int userId = Integer.parseInt(request.getParameter("userId"));
        String newPassword = "NewPass123"; // Generate or allow admin input
        boolean success = userDAO.resetPassword(userId, newPassword);
        if (success) {
            logger.info("🔑 Password reset for User ID " + userId);
            request.getSession().setAttribute("message", "Password reset successfully. New Password: " + newPassword);
        } else {
            logger.warning("❌ Failed to reset password for User ID: " + userId);
            request.getSession().setAttribute("errorMessage", "Password reset failed.");
        }
        response.sendRedirect("AdminServlet?action=manageUsers");
    }

    private void handleDeleteUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int userId = Integer.parseInt(request.getParameter("userId"));
        boolean success = userDAO.deleteUser(userId);
        if (success) {
            logger.info("🗑️ User ID " + userId + " deleted.");
            request.getSession().setAttribute("message", "User deleted successfully.");
        } else {
            logger.warning("❌ Failed to delete User ID: " + userId);
            request.getSession().setAttribute("errorMessage", "User deletion failed.");
        }
        response.sendRedirect("AdminServlet?action=manageUsers");
    }
}
