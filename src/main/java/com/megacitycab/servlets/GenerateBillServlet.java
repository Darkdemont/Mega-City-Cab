package com.megacitycab.servlets;

import com.megacitycab.dao.BookingDAO;
import com.megacitycab.models.Booking;
import com.megacitycab.utils.BillCalculator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/GenerateBillServlet")
public class GenerateBillServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(GenerateBillServlet.class.getName());
    private BookingDAO bookingDAO;

    @Override
    public void init() {
        bookingDAO = new BookingDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String bookingIdParam = request.getParameter("bookingId");

        if (bookingIdParam == null || bookingIdParam.isEmpty()) {
            logger.warning("❌ Invalid booking ID.");
            response.sendRedirect("customerIndex.jsp?error=InvalidBooking");
            return;
        }

        int bookingId;
        try {
            bookingId = Integer.parseInt(bookingIdParam);
        } catch (NumberFormatException e) {
            logger.warning("❌ Booking ID must be a number.");
            response.sendRedirect("customerIndex.jsp?error=InvalidBookingFormat");
            return;
        }


        Booking booking = null;
        int attempts = 3; // Retry up to 3 times
        while (attempts > 0) {
            booking = bookingDAO.getBookingById(bookingId);
            if (booking != null && "Completed".equals(booking.getStatus())) {
                break;
            }
            try {
                Thread.sleep(500); // Wait for 500ms before retrying
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            attempts--;
        }

        if (booking == null || !"Completed".equals(booking.getStatus())) {
            logger.warning("❌ Booking not found or not completed.");
            response.sendRedirect("customerIndex.jsp?error=BookingNotCompleted");
            return;
        }


        double totalFare = BillCalculator.calculateTotal(booking.getDistanceKm(), 0);
        boolean fareUpdated = bookingDAO.updateBookingFare(bookingId, totalFare);

        if (!fareUpdated) {
            logger.severe("❌ Failed to update fare in database.");
            response.sendRedirect("customerIndex.jsp?error=DatabaseError");
            return;
        }

        logger.info("✅ Bill generated for Booking ID: " + bookingId);


        response.sendRedirect(request.getContextPath() + "/calculateBill.jsp?success=1" +

                "&booking_id=" + bookingId +
                "&pickup=" + booking.getPickupLocation() +
                "&destination=" + booking.getDestination() +
                "&distance=" + booking.getDistanceKm() +
                "&total=" + totalFare +
                "&tax=" + (totalFare * 0.10) + // 10% tax
                "&discount=" + (totalFare * 0.05) + // 5% discount
                "&netAmount=" + (totalFare + (totalFare * 0.10) - (totalFare * 0.05)));
    }
}
