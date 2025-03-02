<%@ page import="java.util.List" %>
<%@ page import="com.megacitycab.models.Booking" %>
<%@ page import="com.megacitycab.models.Car" %>
<%@ page import="com.megacitycab.models.Driver" %>
<%@ page import="jakarta.servlet.http.HttpSession" %>
<%@ page import="com.megacitycab.models.User" %>
<%@ page session="true" %>

<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Customer Dashboard</title>
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
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

<h2>Welcome, <%= customer.getUsername() %>!</h2>
<a href="LogoutServlet" class="btn btn-danger">Logout</a>

<hr>

<!-- ✅ Display Success/Error Messages -->
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

<!-- ✅ Booking Form -->
<h3>Book a Ride</h3>
<form action="BookingServlet" method="POST" onsubmit="return validateForm()">
  <div class="row g-3">
    <div class="col-md-3">
      <input type="text" name="pickupLocation" id="pickupLocation" class="form-control" placeholder="Pickup Location" required>
    </div>
    <div class="col-md-3">
      <input type="text" name="destination" id="destination" class="form-control" placeholder="Destination" required>
    </div>
    <div class="col-md-2">
      <select name="carType" class="form-select" required>
        <option value="Standard">Standard</option>
        <option value="Luxury">Luxury</option>
      </select>
    </div>
    <div class="col-md-2">
      <input type="datetime-local" name="dateTime" id="dateTime" class="form-control" required>
    </div>
    <div class="col-md-2">
      <input type="number" name="distanceKm" id="distanceKm" class="form-control" placeholder="Distance (km)" min="1" required>
    </div>
    <div class="col-md-12 mt-2">
      <button type="submit" class="btn btn-primary">Book Now</button>
    </div>
  </div>
</form>

<hr>

<!-- ✅ Booking History -->
<h3>Your Bookings</h3>
<table class="table table-striped">
  <thead>
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
      <form action="BookingServlet" method="POST" style="display:inline;">
        <input type="hidden" name="action" value="cancel">
        <input type="hidden" name="bookingId" value="<%= booking.getBookingId() %>">
        <button type="submit" class="btn btn-danger btn-sm">Cancel</button>
      </form>
      <% } else if ("Completed".equals(booking.getStatus())) { %>
      <form action="GenerateBillServlet" method="GET" style="display:inline;">
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

<!-- ✅ Available Cars & Drivers -->
<h3>Available Cars & Drivers</h3>
<div class="row">
  <div class="col-md-6">
    <h4>Available Cars</h4>
    <ul class="list-group">
      <%
        List<Car> availableCars = (List<Car>) request.getAttribute("availableCars");
        if (availableCars != null && !availableCars.isEmpty()) {
          for (Car car : availableCars) {
      %>
      <li class="list-group-item"><%= car.getCarModel() %> - <%= car.getCarStatus() %></li>
      <%
        } } else {
      %>
      <li class="list-group-item text-muted">No cars available.</li>
      <% } %>
    </ul>
  </div>

  <div class="col-md-6">
    <h4>Available Drivers</h4>
    <ul class="list-group">
      <%
        List<Driver> availableDrivers = (List<Driver>) request.getAttribute("availableDrivers");
        if (availableDrivers != null && !availableDrivers.isEmpty()) {
          for (Driver driver : availableDrivers) {
      %>
      <li class="list-group-item"><%= driver.getDriverName() %> - <%= driver.getDriverStatus() %></li>
      <%
        } } else {
      %>
      <li class="list-group-item text-muted">No drivers available.</li>
      <% } %>
    </ul>
  </div>
</div>

<!-- ✅ JavaScript for Form Validation -->
<script>
  function validateForm() {
    let pickup = document.getElementById("pickupLocation").value;
    let destination = document.getElementById("destination").value;
    let dateTime = document.getElementById("dateTime").value;
    let distance = document.getElementById("distanceKm").value;

    if (pickup === "" || destination === "" || dateTime === "" || distance <= 0) {
      alert("❌ All fields are required and distance must be greater than 0.");
      return false;
    }
    return true;
  }
</script>

<!-- ✅ Bootstrap Script -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>