package com.megacitycab.authentication;

import com.megacitycab.dao.UserDAO;
import com.megacitycab.models.User;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/SignupServlet")
public class SignupServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserDAO userDAO;

    @Override
    public void init() {
        userDAO = new UserDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("📌 SignupServlet triggered!");

        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        System.out.println("📌 Received Data - Username: " + username + ", Email: " + email);

        // Validate input fields
        if (username == null || email == null || password == null ||
                username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            System.out.println("❌ Error: Missing input fields!");
            response.sendRedirect("signup.jsp?error=All fields are required!");
            return;
        }

        // Hash the password before storing
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));
        System.out.println("📌 Hashed Password: " + hashedPassword);

        User newUser = new User(username, email, hashedPassword, "Customer");

        try {
            if (userDAO.isUserExists(username, email)) {
                System.out.println("❌ Error: Username or Email already exists!");
                response.sendRedirect("signup.jsp?error=Username or Email already exists!");
                return;
            }

            boolean userCreated = userDAO.createUser(newUser);
            if (userCreated) {
                System.out.println("✅ User successfully inserted into the database!");
                response.sendRedirect("login.jsp?success=Account created successfully!");
            } else {
                System.out.println("❌ Error: User insertion failed!");
                response.sendRedirect("signup.jsp?error=Could not create account. Try again!");
            }
        } catch (Exception e) {
            System.out.println("❌ Exception in SignupServlet:");
            e.printStackTrace();
            response.sendRedirect("signup.jsp?error=Internal error. Please try again later.");
        }
    }
}
