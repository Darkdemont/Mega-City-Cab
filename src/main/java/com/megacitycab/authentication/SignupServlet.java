package com.megacitycab.authentication;

import com.megacitycab.dao.UserDAO;
import com.megacitycab.models.User;
import org.mindrot.jbcrypt.BCrypt;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.regex.Pattern;

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


        String username = request.getParameter("username") != null ? request.getParameter("username").trim() : "";
        String email = request.getParameter("email") != null ? request.getParameter("email").trim() : "";
        String password = request.getParameter("password") != null ? request.getParameter("password").trim() : "";
        String role = "Customer"; // ✅ Automatically assign role

        System.out.println("📌 Received Data - Username: " + username + ", Email: " + email);


        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            System.out.println("❌ Error: Missing input fields!");
            response.sendRedirect("signup.jsp?error=All fields are required!");
            return;
        }

        if (username.length() < 3) {
            System.out.println("❌ Error: Username too short!");
            response.sendRedirect("signup.jsp?error=Username must be at least 3 characters long!");
            return;
        }

        if (!isValidEmail(email)) {
            System.out.println("❌ Error: Invalid email format!");
            response.sendRedirect("signup.jsp?error=Invalid email format!");
            return;
        }

        if (password.length() < 6) {
            System.out.println("❌ Error: Weak password!");
            response.sendRedirect("signup.jsp?error=Password must be at least 6 characters long!");
            return;
        }

        try {
            if (userDAO.isUserExists(username, email)) {
                System.out.println("Error: Username or Email already exists!");
                response.sendRedirect("signup.jsp?error=Username or Email already exists!");
                return;
            }


            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));
            System.out.println(" Hashed Password: " + hashedPassword);


            User newUser = new User(username, email, hashedPassword, role);

            boolean userCreated = userDAO.createUser(newUser);
            if (userCreated) {
                System.out.println(" User successfully inserted into the database!");
                response.sendRedirect("login.jsp?success=Account created successfully! Please login.");
            } else {
                System.out.println("error: User insertion failed!");
                response.sendRedirect("signup.jsp?error=Could not create account. Try again!");
            }
        } catch (Exception e) {
            System.out.println("❌ Exception in SignupServlet:");
            e.printStackTrace();
            response.sendRedirect("signup.jsp?error=Internal error. Please try again later.");
        }
    }


    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return Pattern.compile(emailRegex).matcher(email).matches();
    }
}
