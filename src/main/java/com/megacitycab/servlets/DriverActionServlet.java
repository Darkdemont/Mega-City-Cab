package com.megacitycab.servlets;

import com.megacitycab.dao.DatabaseConnection;
import com.megacitycab.models.Booking;
import com.megacitycab.models.Driver;
import com.megacitycab.models.User;
import com.megacitycab.dao.BookingDAO;
import com.megacitycab.dao.DriverDAO;
import com.megacitycab.dao.UserDAO;
import com.megacitycab.utils.EmailUtil;
import com.megacitycab.utils.SmsUtil;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.megacitycab.dao.BillingDAO;
import com.megacitycab.services.BillingService;
import com.megacitycab.models.Payment;
import java.math.BigDecimal;

@WebServlet("/DriverActionServlet")
public class DriverActionServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(DriverActionServlet.class.getName());

    private BookingDAO bookingDAO;
    private DriverDAO driverDAO;
    private UserDAO userDAO;

    @Override
    public void init() {
        bookingDAO = new BookingDAO();
        driverDAO = new DriverDAO();
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);

        if (session == null) {
            logger.severe("❌ Session is missing!");
            response.sendRedirect("login.jsp?error=SessionExpired");
            return;
        }

        Integer driverId = (Integer) session.getAttribute("driverId");
        if (driverId == null) {
            logger.severe("❌ Driver session expired or missing!");
            response.sendRedirect("login.jsp?error=SessionExpired");
            return;
        }

        String action = request.getParameter("action");
        String bookingIdStr = request.getParameter("bookingId");

        if (bookingIdStr == null || bookingIdStr.isEmpty()) {
            logger.severe("❌ Missing bookingId in request!");
            response.sendRedirect("driverIndex.jsp?error=InvalidBooking");
            return;
        }

        int bookingId;
        try {
            bookingId = Integer.parseInt(bookingIdStr);
        } catch (NumberFormatException e) {
            logger.log(Level.SEVERE, "❌ Invalid bookingId format: " + bookingIdStr, e);
            response.sendRedirect("driverIndex.jsp?error=InvalidBooking");
            return;
        }

        Booking booking = bookingDAO.getBookingById(bookingId);
        if (booking == null) {
            logger.severe("❌ Booking not found for ID: " + bookingId);
            response.sendRedirect("driverIndex.jsp?error=BookingNotFound");
            return;
        }

        User customer = userDAO.getUserById(booking.getCustomerId());
        Driver driver = driverDAO.getDriverById(driverId);
        boolean success = false;

        switch (action) {
            case "accept":
                if (!"Upcoming".equals(booking.getStatus())) {
                    logger.warning("🚨 Booking " + bookingId + " is not in 'Upcoming' state. Cannot accept.");
                    response.sendRedirect("driverIndex.jsp?error=AlreadyProcessed");
                    return;
                }

                if (booking.getDriverId() > 0) {
                    logger.warning("🚨 Booking " + bookingId + " is already assigned to another driver!");
                    response.sendRedirect("driverIndex.jsp?error=AlreadyAssigned");
                    return;
                }

                success = bookingDAO.assignDriver(bookingId, driverId, "Accepted");
                if (success) {
                    driverDAO.markDriverOnDuty(driverId);
                    logger.info("✅ Booking " + bookingId + " accepted by Driver " + driverId);


                    String subject = "Driver Assigned to Your Booking";
                    String message = "Dear " + customer.getUsername() + ",\nA driver has accepted your booking.\nDriver: " + driver.getDriverName();
                    SmsUtil.sendSms(customer.getMobile(), "Your driver " + driver.getDriverName() + " has accepted your booking.");


                    EmailUtil.sendEmail(customer.getEmail(), subject, message);
                    SmsUtil.sendSms(customer.getMobile(), "Your driver " + driver.getDriverName() + " has accepted your booking.");

                    session.setAttribute("message", "Booking accepted. Notification sent to the customer.");
                    response.sendRedirect("DriverDashboardServlet?success=BookingAccepted&bookingId=" + bookingId);
                } else {
                    logger.warning("❌ Failed to assign booking " + bookingId + " to Driver " + driverId);
                    response.sendRedirect("driverIndex.jsp?error=AssignmentFailed");
                }
                return;

            case "reject":
                if (!"Upcoming".equals(booking.getStatus())) {
                    logger.warning("🚨 Booking " + bookingId + " is not in 'Upcoming' state. Cannot reject.");
                    response.sendRedirect("driverIndex.jsp?error=AlreadyProcessed");
                    return;
                }

                success = bookingDAO.unassignDriver(bookingId);
                if (success) {
                    int newDriverId = driverDAO.getAvailableDriver();
                    if (newDriverId > 0) {
                        bookingDAO.assignDriver(bookingId, newDriverId, "Upcoming");
                        logger.info("🔄 Booking " + bookingId + " reassigned to new Driver ID: " + newDriverId);
                    } else {
                        logger.info("🚫 No available driver to reassign Booking ID: " + bookingId);
                    }

                    response.sendRedirect("DriverDashboardServlet?success=BookingRejected&bookingId=" + bookingId);
                } else {
                    logger.warning("❌ Failed to unassign booking " + bookingId);
                    response.sendRedirect("driverIndex.jsp?error=UnassignFailed");
                }
                return;

            case "complete":
                if (!"Accepted".equals(booking.getStatus())) {
                    logger.warning("🚨 Booking " + bookingId + " is not in 'Accepted' state. Cannot complete.");
                    response.sendRedirect("driverIndex.jsp?error=NotAccepted");
                    return;
                }

                if (booking.getDriverId() != driverId) {
                    logger.warning("🚨 Driver " + driverId + " is trying to complete a booking assigned to another driver!");
                    response.sendRedirect("driverIndex.jsp?error=Unauthorized");
                    return;
                }

                success = bookingDAO.updateBookingStatus(bookingId, "Completed");

                if (success) {
                    driverDAO.markDriverAvailable(driverId);
                    logger.info("✅ Booking " + bookingId + " marked as Completed by Driver " + driverId);

                    BillingService billingService = new BillingService();
                    BigDecimal totalFare = billingService.calculateTotalFare(booking.getDistanceKm());

                    BillingDAO billingDAO = new BillingDAO(DatabaseConnection.getConnection());
                    boolean isBillGenerated = billingDAO.savePayment(new Payment(
                            bookingId,
                            booking.getCustomerId(),
                            totalFare,
                            "Cash",
                            "Pending"
                    ));

                    if (isBillGenerated) {
                        logger.info("✅ Bill generated for Booking ID: " + bookingId);
                        response.sendRedirect("DriverDashboardServlet?success=BookingCompleted&bookingId=" + bookingId);
                    } else {
                        logger.warning("❌ Bill generation failed for Booking ID: " + bookingId);
                        response.sendRedirect("driverIndex.jsp?error=BillGenerationFailed");
                    }
                } else {
                    logger.severe("❌ Failed to update booking status for bookingId: " + bookingId);
                    response.sendRedirect("driverIndex.jsp?error=UpdateFailed");
                }
                return;
        }
    }
}
