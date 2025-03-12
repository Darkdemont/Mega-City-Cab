package com.megacitycab.servlets;

import com.megacitycab.dao.UserDAO;
import com.megacitycab.models.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/ProfileUpdateServlet")
public class ProfileUpdateServlet extends HttpServlet {
    private UserDAO userDAO;

    @Override
    public void init() {
        userDAO = new UserDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User customer = (User) session.getAttribute("loggedInUser");

        if (customer == null) {
            response.sendRedirect("index.jsp");
            return;
        }


        String mobile = request.getParameter("mobile");
        String nic = request.getParameter("nic");
        String address = request.getParameter("address");

        System.out.println("📌 Received Data - Mobile: " + mobile + ", NIC: " + nic + ", Address: " + address);
        System.out.println("📌 Updating User ID: " + customer.getUserId());

        boolean updated = userDAO.updateCustomerDetails(customer.getUserId(), mobile, nic, address);

        if (updated) {

            System.out.println("✅ Profile Updated Successfully in Database!");
            session.setAttribute("message", "Profile updated successfully!");


            customer.setMobile(mobile);
            customer.setNic(nic);
            customer.setAddress(address);
            session.setAttribute("loggedInUser", customer);
        } else {
            System.out.println("❌ Profile Update Failed!");
            session.setAttribute("errorMessage", "Profile update failed.");
        }

        response.sendRedirect("profile.jsp");
    }

}
