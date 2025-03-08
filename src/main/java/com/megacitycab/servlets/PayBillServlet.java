package com.megacitycab.servlets;

import com.megacitycab.dao.BillingDAO;
import com.megacitycab.dao.DatabaseConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/PayBillServlet")
public class PayBillServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(PayBillServlet.class.getName());

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String bookingIdStr = request.getParameter("bookingId");


        if (bookingIdStr == null || bookingIdStr.isEmpty()) {
            logger.severe("❌ Missing bookingId for payment processing.");
            response.sendRedirect("calculateBill.jsp?error=InvalidBooking");
            return;
        }

        int bookingId;
        try {
            bookingId = Integer.parseInt(bookingIdStr);
        } catch (NumberFormatException e) {
            logger.severe("❌ Invalid bookingId format: " + bookingIdStr);
            response.sendRedirect("calculateBill.jsp?error=InvalidBooking");
            return;
        }


        try (Connection conn = DatabaseConnection.getConnection()) {
            BillingDAO billingDAO = new BillingDAO(conn);
            boolean isPaid = billingDAO.updatePaymentStatus(bookingId, "Completed");

            if (isPaid) {
                logger.info("✅ Payment successfully recorded for Booking ID: " + bookingId);
                response.sendRedirect("calculateBill.jsp?success=PaymentCompleted&booking_id=" + bookingId);
            } else {
                logger.warning("❌ Payment processing failed for Booking ID: " + bookingId);
                response.sendRedirect("calculateBill.jsp?error=PaymentFailed&booking_id=" + bookingId);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "❌ Server error while processing payment: ", e);
            response.sendRedirect("calculateBill.jsp?error=ServerError&booking_id=" + bookingId);
        }
    }
}
