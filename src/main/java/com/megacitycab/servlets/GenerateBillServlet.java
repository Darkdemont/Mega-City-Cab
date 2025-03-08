package com.megacitycab.servlets;

import com.megacitycab.dao.BookingDAO;
import com.megacitycab.models.Booking;
import com.megacitycab.utils.BillCalculator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import java.io.IOException;
import java.io.OutputStream;
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
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid booking ID.");
            return;
        }

        int bookingId;
        try {
            bookingId = Integer.parseInt(bookingIdParam);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Booking ID must be a number.");
            return;
        }

        Booking booking = bookingDAO.getBookingById(bookingId);
        if (booking == null || !"Completed".equals(booking.getStatus())) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Booking not found or not completed.");
            return;
        }

        // ✅ Use `BillCalculator` for accurate fare calculation
        double totalFare = BillCalculator.calculateTotal(booking.getDistanceKm(), 0);

        // ✅ Store the calculated fare in the database
        boolean fareUpdated = bookingDAO.updateBookingFare(bookingId, totalFare);
        if (!fareUpdated) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to update fare in database.");
            return;
        }

        logger.info("✅ Generating bill for Booking ID: " + bookingId);

        // ✅ Set response type for PDF download
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=Ride_Bill_" + bookingId + ".pdf");

        try (PDDocument document = new PDDocument();
             OutputStream outputStream = response.getOutputStream()) {

            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
                contentStream.beginText();
                contentStream.newLineAtOffset(100, 750);
                contentStream.showText("MegaCityCab Ride Invoice");

                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.newLineAtOffset(0, -30);
                contentStream.showText("Booking ID: " + booking.getBookingId());
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Pickup Location: " + booking.getPickupLocation());
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Destination: " + booking.getDestination());
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Car Type: " + booking.getCarType());
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Date & Time: " + booking.getDateTime());
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Distance: " + booking.getDistanceKm() + " km");

                // ✅ Show Total Fare from `BillCalculator`
                contentStream.newLineAtOffset(0, -40);
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.showText("Total Fare: $" + String.format("%.2f", totalFare));

                contentStream.newLineAtOffset(0, -40);
                contentStream.setFont(PDType1Font.HELVETICA_OBLIQUE, 12);
                contentStream.showText("Thank you for choosing MegaCityCab!");
                contentStream.endText();
            }

            document.save(outputStream);
            logger.info("✅ PDF generated successfully for Booking ID: " + bookingId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "❌ Error generating PDF for Booking ID: " + bookingId, e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error generating PDF.");
        }
    }
}
