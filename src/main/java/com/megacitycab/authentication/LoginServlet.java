package com.megacitycab.authentication;

import com.megacitycab.dao.UserDAO;
import com.megacitycab.models.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserDAO userDAO;

    @Override
    public void init() {
        userDAO = new UserDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        User user = userDAO.getUserByUsername(username);

        if (user != null && userDAO.validateUser(username, password)) {
            HttpSession session = request.getSession();
            session.setAttribute("username", username);
            session.setAttribute("role", user.getRole());

            // Redirect based on user role
            switch (user.getRole()) {
                case "Admin":
                    response.sendRedirect("adminIndex.jsp"); // Redirect to admin dashboard
                    break;
                case "Customer":
                    response.sendRedirect("customerIndex.jsp"); // Redirect to customer dashboard
                    break;
                case "Driver":
                    response.sendRedirect("driverIndex.jsp"); // Redirect to driver dashboard
                    break;
                default:
                    response.sendRedirect("login.jsp?error=Unauthorized role");
            }
        } else {
            response.sendRedirect("login.jsp?error=Invalid credentials");
        }
    }
}
