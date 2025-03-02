package com.megacitycab.servlets;

import com.megacitycab.dao.BookingDAO;
import com.megacitycab.dao.CarDAO;
import com.megacitycab.dao.DriverDAO;
import com.megacitycab.models.Booking;
import com.megacitycab.models.Car;
import com.megacitycab.models.Driver;
import com.megacitycab.models.User;
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
        HttpSession session = request.getSession(false); // Prevent creating a new session
        if (session == null || session.getAttribute("loggedInUser") == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        User customer = (User) session.getAttribute("loggedInUser");

        logger.info("📌 Fetching bookings for customer ID: " + customer.getUserId());

        List<Booking> bookings = bookingDAO.getBookingsByCustomerId(customer.getUserId());
        List<Car> availableCars = carDAO.getAvailableCars();
        List<Driver> availableDrivers = driverDAO.getAvailableDrivers();

        request.setAttribute("bookings", bookings);
        request.setAttribute("availableCars", availableCars);
        request.setAttribute("availableDrivers", availableDrivers);

        logger.info("✅ Sending customer to customerIndex.jsp with updated data.");
        request.getRequestDispatcher("customerIndex.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false); // Prevent creating a new session
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
            boolean success = bookingDAO.cancelBooking(bookingId);
            if (success) {
                logger.info("✅ Booking ID " + bookingId + " canceled successfully.");
                request.getSession().setAttribute("message", "Booking canceled successfully.");
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
        String carType = request.getParameter("carType");
        String dateTime = request.getParameter("dateTime");
        String distanceStr = request.getParameter("distanceKm");

        if (isInvalidBookingData(pickupLocation, destination, carType, dateTime, distanceStr)) {
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

        // 🚖 **Find an available driver automatically**
        List<Driver> availableDrivers = driverDAO.getAvailableDrivers();
        int assignedDriverId = availableDrivers.isEmpty() ? -1 : availableDrivers.get(0).getDriverId();

        Booking booking = new Booking(customer.getUserId(), pickupLocation, destination, carType, dateTime, distanceKm, "Upcoming", assignedDriverId);

        logger.info("🛠️ Attempting to add booking for customer ID: " + customer.getUserId());

        boolean success = bookingDAO.addBooking(booking);

        if (success) {
            logger.info("✅ Booking successfully added for customer ID: " + customer.getUserId());

            // ✅ Assign driver and update driver status
            if (assignedDriverId > 0) {
                bookingDAO.assignDriver(booking.getBookingId(), assignedDriverId);
                driverDAO.markDriverOnDuty(assignedDriverId);
                logger.info("🚖 Assigned driver ID: " + assignedDriverId + " to booking.");
            }

            request.getSession().setAttribute("message", "Booking successful.");
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