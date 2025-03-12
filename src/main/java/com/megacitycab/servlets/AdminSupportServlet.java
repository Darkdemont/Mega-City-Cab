package com.megacitycab.servlets;

import com.megacitycab.dao.SupportTicketDAO;
import com.megacitycab.models.SupportTicket;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/AdminSupportServlet")
public class AdminSupportServlet extends HttpServlet {
    private SupportTicketDAO supportTicketDAO;

    @Override
    public void init() {
        supportTicketDAO = new SupportTicketDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<SupportTicket> tickets = supportTicketDAO.getAllSupportTickets();
        request.setAttribute("supportTickets", tickets);
        request.getRequestDispatcher("adminDashboard.jsp?action=manageSupportTickets").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("updateTicketStatus".equals(action)) {
            int ticketId = Integer.parseInt(request.getParameter("ticketId"));
            String newStatus = request.getParameter("status");

            boolean updated = supportTicketDAO.updateTicketStatus(ticketId, newStatus);

            if (updated) {
                request.getSession().setAttribute("message", "✅ Support ticket status updated!");
            } else {
                request.getSession().setAttribute("errorMessage", "❌ Failed to update ticket status.");
            }
        }

        response.sendRedirect("AdminSupportServlet");
    }
}
