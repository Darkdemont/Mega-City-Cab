<%@ page import="java.util.List" %>
<%@ page import="com.megacitycab.models.Booking, com.megacitycab.models.Car, com.megacitycab.models.Driver, com.megacitycab.models.User" %>
<%@ page import="jakarta.servlet.http.HttpSession" %>
<%@ page session="true" %>

<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Customer Dashboard</title>


  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">

  <link rel="stylesheet" href="assets/css/customerDashboard.css">
</head>
<body class="container mt-4">

<%
  HttpSession userSession = request.getSession(false);
  User customer = (userSession != null) ? (User) userSession.getAttribute("loggedInUser") : null;
  if (customer == null) {
    response.sendRedirect("index.jsp");
    return;
  }
%>



<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
  <div class="container">
    <a class="navbar-brand" href="index.jsp"> MegaCityCab</a>
    <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
      <span class="navbar-toggler-icon"></span>
    </button>
    <div class="collapse navbar-collapse" id="navbarNav">
      <ul class="navbar-nav ms-auto">
        <li class="nav-item"><a class="nav-link" href="index.jsp">Home</a></li>
        <li class="nav-item"><a class="nav-link" href="about.jsp">About</a></li>
        <li class="nav-item"><a class="nav-link" href="services.jsp">Services</a></li>
        <li class="nav-item"><a class="nav-link" href="manageBookings.jsp">Manage Bookings</a></li>
        <li class="nav-item"><a class="nav-link" href="payment.jsp">Payments</a></li>
        <li class="nav-item"><a class="nav-link" href="contact.jsp">Contact</a></li>


        <li class="nav-item">
          <span class="nav-link text-warning">Welcome, <%= customer.getUsername() %>!</span>
        </li>
        <li class="nav-item">
          <a class="nav-link btn btn-danger text-white ms-2" href="LogoutServlet">Logout</a>
        </li>
      </ul>
    </div>
  </div>
</nav>


<hr>


<%
  String message = (String) session.getAttribute("message");
  String errorMessage = (String) session.getAttribute("errorMessage");

  if (message != null) {
%>
<div class="alert alert-success"><%= message %></div>
<%
    session.removeAttribute("message");
  }

  if (errorMessage != null) {
%>
<div class="alert alert-danger"><%= errorMessage %></div>
<%
    session.removeAttribute("errorMessage");
  }
%>


<div class="card p-4 shadow-sm">
  <h3 class="text-center text-dark mb-3">Book a Ride</h3>
  <form action="BookingServlet" method="POST" id="bookingForm">
    <div class="row g-3">
      <div class="col-md-4">
        <label class="form-label">Pickup Location</label>
        <input type="text" name="pickupLocation" id="pickupLocation" class="form-control" placeholder="Enter Pickup Location" required>
      </div>
      <div class="col-md-4">
        <label class="form-label">Destination</label>
        <input type="text" name="destination" id="destination" class="form-control" placeholder="Enter Destination" required>
      </div>


      <div class="col-md-4">
        <label class="form-label">Car Type</label>
        <select name="carType" class="form-select" required>
          <%
            List<Car> availableCars = (List<Car>) request.getAttribute("availableCars");
            if (availableCars != null && !availableCars.isEmpty()) {
              for (Car car : availableCars) {
          %>
          <option value="<%= car.getCarModel() %>"><%= car.getCarModel() %></option>
          <%
            }
          } else {
          %>
          <option value="" disabled>No Cars Available</option>
          <%
            }
          %>
        </select>
      </div>

      <div class="col-md-4">
        <label class="form-label">Date & Time</label>
        <input type="datetime-local" name="dateTime" id="dateTime" class="form-control" required>
      </div>
      <div class="col-md-4">
        <label class="form-label">Distance (km)</label>
        <input type="number" name="distanceKm" id="distanceKm" class="form-control" placeholder="Distance in km" min="1" required>
      </div>
      <div class="col-md-12 mt-3 text-center">
        <button type="submit" class="btn btn-primary w-50">Book Now</button>
      </div>
    </div>
  </form>
</div>

<hr>


<h3 class="text-dark">Your Bookings</h3>
<table class="table table-bordered table-hover">
  <thead class="table-dark">
  <tr>
    <th>Booking ID</th>
    <th>Pickup</th>
    <th>Destination</th>
    <th>Status</th>
    <th>Driver</th>
    <th>Action</th>
  </tr>
  </thead>
  <tbody>
  <%
    List<Booking> bookings = (List<Booking>) request.getAttribute("bookings");
    if (bookings != null && !bookings.isEmpty()) {
      for (Booking booking : bookings) {
  %>
  <tr>
    <td><%= booking.getBookingId() %></td>
    <td><%= booking.getPickupLocation() %></td>
    <td><%= booking.getDestination() %></td>
    <td><%= booking.getStatus() %></td>
    <td>
      <% if (booking.getDriverId() > 0) { %>
      Driver ID: <%= booking.getDriverId() %>
      <% } else { %>
      <span class="text-muted">No driver assigned</span>
      <% } %>
    </td>
    <td>
      <% if ("Upcoming".equals(booking.getStatus())) { %>
      <form action="BookingServlet" method="POST" class="d-inline">
        <input type="hidden" name="action" value="cancel">
        <input type="hidden" name="bookingId" value="<%= booking.getBookingId() %>">
        <button type="submit" class="btn btn-danger btn-sm">Cancel</button>
      </form>
      <% } else if ("Completed".equals(booking.getStatus())) { %>
      <form action="GenerateBillServlet" method="GET" class="d-inline">
        <input type="hidden" name="bookingId" value="<%= booking.getBookingId() %>">
        <button type="submit" class="btn btn-primary btn-sm">Print Bill</button>
      </form>
      <% } %>
    </td>
  </tr>
  <%
    }
  } else {
  %>
  <tr>
    <td colspan="6" class="text-center text-muted">No bookings found.</td>
  </tr>
  <% } %>
  </tbody>
</table>

<hr>


<div class="row">
  <div class="col-md-6">
    <div class="card shadow-sm p-3">
      <h4 class="text-primary">Available Cars</h4>
      <ul class="list-group">
        <%
          if (availableCars != null && !availableCars.isEmpty()) {
            for (Car car : availableCars) {
        %>
        <li class="list-group-item d-flex justify-content-between align-items-center">
          <span><strong><%= car.getCarModel() %></strong> - <%= car.getCarStatus() %></span>
          <span class="badge bg-success">Available</span>
        </li>
        <%
          } } else {
        %>
        <li class="list-group-item text-muted text-center">No cars available.</li>
        <% } %>
      </ul>
    </div>
  </div>

  <div class="col-md-6">
    <div class="card shadow-sm p-3">
      <h4 class="text-success">Available Drivers</h4>
      <ul class="list-group">
        <%
          List<Driver> availableDrivers = (List<Driver>) request.getAttribute("availableDrivers");
          if (availableDrivers != null && !availableDrivers.isEmpty()) {
            for (Driver driver : availableDrivers) {
        %>
        <li class="list-group-item d-flex justify-content-between align-items-center">
          <span><strong><%= driver.getDriverName() %></strong> - <%= driver.getDriverStatus() %></span>
          <span class="badge bg-primary">Online</span>
        </li>
        <%
          }
        } else {
        %>
        <li class="list-group-item text-muted text-center">No drivers available.</li>
        <% } %>
      </ul>
    </div>
  </div>
</div>


<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>
