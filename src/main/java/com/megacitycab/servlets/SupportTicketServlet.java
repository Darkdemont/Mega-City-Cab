package com.megacitycab.servlets;

import com.megacitycab.dao.SupportTicketDAO;
import com.megacitycab.models.SupportTicket;
import com.megacitycab.models.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/SupportTicketServlet")
public class SupportTicketServlet extends HttpServlet {
    private SupportTicketDAO supportTicketDAO;

    @Override
    public void init() {
        supportTicketDAO = new SupportTicketDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User customer = (User) session.getAttribute("loggedInUser");

        if (customer == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        String subject = request.getParameter("subject");
        String description = request.getParameter("description");

        boolean submitted = supportTicketDAO.createSupportTicket(customer.getUserId(), subject, description);

        if (submitted) {
            session.setAttribute("message", "✅ Your complaint has been submitted successfully!");
        } else {
            session.setAttribute("errorMessage", "❌ Failed to submit complaint. Please try again.");
        }


        List<SupportTicket> userTickets = supportTicketDAO.getTicketsByUserId(customer.getUserId());
        request.setAttribute("userTickets", userTickets);


        request.getRequestDispatcher("helpSupport.jsp").forward(request, response);
    }
}
