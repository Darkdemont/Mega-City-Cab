package com.megacitycab.authentication;

import com.megacitycab.dao.UserDAO;
import com.megacitycab.dao.DriverDAO;
import com.megacitycab.models.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(LoginServlet.class.getName());
    private UserDAO userDAO;
    private DriverDAO driverDAO;

    @Override
    public void init() {
        userDAO = new UserDAO();
        driverDAO = new DriverDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // ✅ Validate user input
        if (isNullOrEmpty(username, password)) {
            logger.warning("❌ Login failed: Empty username or password.");
            response.sendRedirect("login.jsp?error=Empty fields. Please enter both username and password.");
            return;
        }

        // ✅ Retrieve user from database
        User user = userDAO.getUserByUsername(username);

        if (user != null && userDAO.validateUser(username, password)) {
            HttpSession session = request.getSession(true);
            session.setAttribute("loggedInUser", user);
            session.setAttribute("role", user.getRole());
            session.setAttribute("userId", user.getUserId());

            // ✅ Fetch `driver_id` for drivers
            if ("Driver".equals(user.getRole())) {
                int driverId = driverDAO.getDriverIdByUserId(user.getUserId());
                if (driverId > 0) {
                    session.setAttribute("driverId", driverId);
                    logger.info("✅ Driver ID stored in session: " + driverId);
                } else {
                    logger.warning("⚠️ No driver_id found for user_id: " + user.getUserId());
                }
            }

            session.setMaxInactiveInterval(30 * 60); // ✅ Set session timeout (30 min)
            logger.info("✅ Login successful: User " + username + " (" + user.getRole() + ") logged in.");

            // ✅ Redirect user based on their role
            redirectUser(response, user.getRole());

        } else {
            logger.warning("❌ Login failed: Invalid credentials for username " + username);
            response.sendRedirect("login.jsp?error=Invalid credentials. Please try again.");
        }
    }

    // ✅ Utility method to check for null or empty values
    private boolean isNullOrEmpty(String... values) {
        for (String value : values) {
            if (value == null || value.trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    // ✅ Redirect user based on role
    private void redirectUser(HttpServletResponse response, String role) throws IOException {
        switch (role) {
            case "Admin":
                response.sendRedirect("adminIndex.jsp"); // ✅ Admin Dashboard
                break;
            case "Customer":
                response.sendRedirect("customerIndex.jsp"); // ✅ Customer Dashboard
                break;
            case "Driver":
                response.sendRedirect("DriverDashboardServlet"); // ✅ Redirect to Driver Dashboard
                break;
            default:
                logger.warning("❌ Unauthorized role detected: " + role);
                response.sendRedirect("login.jsp?error=Unauthorized role.");
        }
    }
}
