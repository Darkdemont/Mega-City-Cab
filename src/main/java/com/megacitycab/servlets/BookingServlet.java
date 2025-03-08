package com.megacitycab.servlets;

import com.megacitycab.dao.BookingDAO;
import com.megacitycab.dao.CarDAO;
import com.megacitycab.dao.DriverDAO;
import com.megacitycab.models.Booking;
import com.megacitycab.models.Car;
import com.megacitycab.models.Driver;
import com.megacitycab.models.User;
import com.megacitycab.utils.EmailUtil;
import com.megacitycab.utils.SmsUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/BookingServlet")
public class BookingServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(BookingServlet.class.getName());

    private BookingDAO bookingDAO;
    private CarDAO carDAO;
    private DriverDAO driverDAO;

    @Override
    public void init() {
        bookingDAO = new BookingDAO();
        carDAO = new CarDAO();
        driverDAO = new DriverDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loggedInUser") == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        User customer = (User) session.getAttribute("loggedInUser");

        logger.info("📌 Fetching bookings for customer ID: " + customer.getUserId());

        List<Booking> bookings = bookingDAO.getBookingsByCustomerId(customer.getUserId());

        List<Car> availableCars = carDAO.getAvailableCars();
        request.setAttribute("availableCars", availableCars);

        List<Driver> availableDrivers = driverDAO.getAvailableDrivers();
        request.setAttribute("availableDrivers", availableDrivers);

        request.setAttribute("bookings", bookings);

        logger.info("✅ Sending customer to customerIndex.jsp with updated car list.");
        request.getRequestDispatcher("customerIndex.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loggedInUser") == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        User customer = (User) session.getAttribute("loggedInUser");
        String action = request.getParameter("action");

        logger.info("📌 Received POST request: " + action);

        if ("cancel".equals(action)) {
            handleCancelBooking(request, response);
        } else {
            handleNewBooking(request, response, customer);
        }
    }

    private void handleCancelBooking(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int bookingId = Integer.parseInt(request.getParameter("bookingId"));
            Booking booking = bookingDAO.getBookingById(bookingId);

            if (booking == null) {
                logger.warning("❌ Booking ID " + bookingId + " not found.");
                request.getSession().setAttribute("errorMessage", "Booking not found.");
                response.sendRedirect("BookingServlet");
                return;
            }

            // ✅ Allow cancellation only if booking is "Upcoming" or "Accepted"
            if (!"Upcoming".equals(booking.getStatus()) && !"Accepted".equals(booking.getStatus())) {
                logger.warning("🚨 Cannot cancel booking ID " + bookingId + " with status: " + booking.getStatus());
                request.getSession().setAttribute("errorMessage", "Booking cannot be canceled at this stage.");
                response.sendRedirect("BookingServlet");
                return;
            }

            boolean bookingCanceled = bookingDAO.cancelBooking(bookingId);
            Driver driver = driverDAO.getDriverByBookingId(bookingId);

            if (bookingCanceled) {
                logger.info("✅ Booking ID " + bookingId + " canceled successfully.");

                // Notify the driver if assigned
                if (driver != null) {
                    String subject = "Booking Cancelled";
                    String message = "Dear " + driver.getDriverName() + ",\nA customer has cancelled the booking.\nBooking ID: " + bookingId;

                    EmailUtil.sendEmail(driver.getEmail(), subject, message);
                    SmsUtil.sendSms(driver.getMobile(), "Booking ID " + bookingId + " has been cancelled.");
                }

                request.getSession().setAttribute("message", "Booking canceled successfully. Notification sent.");
            } else {
                logger.warning("❌ Failed to cancel booking ID: " + bookingId);
                request.getSession().setAttribute("errorMessage", "Failed to cancel booking.");
            }
        } catch (NumberFormatException e) {
            logger.log(Level.SEVERE, "❌ Invalid booking ID format", e);
            request.getSession().setAttribute("errorMessage", "Invalid booking ID.");
        }
        response.sendRedirect("BookingServlet");
    }

    private void handleNewBooking(HttpServletRequest request, HttpServletResponse response, User customer) throws IOException, ServletException {
        String pickupLocation = request.getParameter("pickupLocation");
        String destination = request.getParameter("destination");
        String selectedCarModel = request.getParameter("carType");
        String dateTime = request.getParameter("dateTime");
        String distanceStr = request.getParameter("distanceKm");

        if (isInvalidBookingData(pickupLocation, destination, selectedCarModel, dateTime, distanceStr)) {
            request.getSession().setAttribute("errorMessage", "All fields are required.");
            response.sendRedirect("BookingServlet");
            return;
        }

        double distanceKm;
        try {
            distanceKm = Double.parseDouble(distanceStr);
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("errorMessage", "Invalid distance format.");
            response.sendRedirect("BookingServlet");
            return;
        }

        Car selectedCar = carDAO.getCarByModel(selectedCarModel);
        if (selectedCar == null) {
            request.getSession().setAttribute("errorMessage", "Selected car is unavailable.");
            response.sendRedirect("BookingServlet");
            return;
        }

        String bookingStatus = "Upcoming";
        Booking booking = new Booking(customer.getUserId(), pickupLocation, destination, selectedCar.getCarModel(), dateTime, distanceKm, bookingStatus, -1);

        logger.info("🛠️ Attempting to add booking for customer ID: " + customer.getUserId());

        boolean bookingCreated = bookingDAO.addBooking(booking);
        int newBookingId = bookingDAO.getLastInsertedBookingId(customer.getUserId());

        if (bookingCreated) {
            logger.info("✅ Booking successfully added with status 'Upcoming' for customer ID: " + customer.getUserId());

            // Send booking confirmation
            String subject = "Booking Confirmation";
            String message = "Dear " + customer.getUsername() + ",\nYour booking has been successfully created.\nBooking ID: " + newBookingId;

            EmailUtil.sendEmail(customer.getEmail(), subject, message);
            SmsUtil.sendSms(customer.getMobile(), "Your booking ID " + newBookingId + " has been confirmed.");

            request.getSession().setAttribute("message", "Booking request submitted. Confirmation sent.");
        } else {
            logger.warning("❌ Booking failed for customer ID: " + customer.getUserId());
            request.getSession().setAttribute("errorMessage", "Booking failed. Please try again.");
        }

        response.sendRedirect("BookingServlet");
    }

    private boolean isInvalidBookingData(String pickup, String destination, String carType, String dateTime, String distance) {
        return pickup == null || pickup.isEmpty() ||
                destination == null || destination.isEmpty() ||
                carType == null || carType.isEmpty() ||
                dateTime == null || dateTime.isEmpty() ||
                distance == null || distance.isEmpty();
    }
}