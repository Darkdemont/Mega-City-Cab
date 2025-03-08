package com.megacitycab.servlets;

import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;

@WebServlet("/DownloadInvoiceServlet")
public class DownloadInvoiceServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String bookingId = request.getParameter("bookingId");
        if (bookingId == null || bookingId.isEmpty()) {
            response.getWriter().println("❌ Invalid Booking ID.");
            return;
        }


        BigDecimal totalFare = new BigDecimal("100.00");
        BigDecimal tax = totalFare.multiply(new BigDecimal("0.10")); // 10% tax
        BigDecimal discount = totalFare.multiply(new BigDecimal("0.05")); // 5% discount
        BigDecimal finalAmount = totalFare.add(tax).subtract(discount);


        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=Invoice_" + bookingId + ".pdf");


        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
                contentStream.beginText();
                contentStream.newLineAtOffset(100, 700);
                contentStream.showText("MegaCityCab - Ride Invoice");
                contentStream.endText();

                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(100, 650);
                contentStream.showText("Booking ID: " + bookingId);
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Total Fare: $" + totalFare);
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Tax (10%): $" + tax);
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Discount (5%): $" + discount);
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Final Amount: $" + finalAmount);
                contentStream.endText();
            }


            try (OutputStream outputStream = response.getOutputStream()) {
                document.save(outputStream);
            }
        }
    }
}
