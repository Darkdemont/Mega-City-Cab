<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="jakarta.servlet.http.HttpSession" %>
<%@ page import="java.util.List, java.net.URLEncoder, java.nio.charset.StandardCharsets, com.megacitycab.models.Booking" %>

<%
  HttpSession sessionDriver = request.getSession(false);

  // ✅ Ensure session exists and driver is logged in
  if (sessionDriver == null || sessionDriver.getAttribute("driverId") == null) {
    response.sendRedirect("login.jsp?error=SessionExpired");
    return;
  }

  // ✅ Retrieve driver details from session
  Integer driverId = (Integer) sessionDriver.getAttribute("driverId");
  String driverUsername = (String) sessionDriver.getAttribute("username");

  if (driverId == null || driverId <= 0) {
    response.sendRedirect("login.jsp?error=InvalidDriver");
    return;
  }

  // ✅ Retrieve assigned bookings and available (unassigned) bookings from request
  List<Booking> assignedBookings = (List<Booking>) request.getAttribute("bookings");
  List<Booking> availableBookings = (List<Booking>) request.getAttribute("availableBookings");
%>

<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Driver Dashboard</title>
  <style>
    body {
      font-family: Arial, sans-serif;
      margin: 20px;
      padding: 0;
      background-color: #f4f4f4;
    }
    h2, h3 {
      color: #333;
    }
    .container {
      max-width: 900px;
      background: white;
      padding: 20px;
      margin: auto;
      box-shadow: 0px 0px 10px rgba(0, 0, 0, 0.1);
      border-radius: 8px;
    }
    table {
      width: 100%;
      border-collapse: collapse;
      margin-top: 20px;
    }
    table, th, td {
      border: 1px solid #ddd;
    }
    th, td {
      padding: 12px;
      text-align: left;
    }
    th {
      background-color: #333;
      color: white;
    }
    .actions a {
      text-decoration: none;
      color: #007bff;
      font-weight: bold;
      margin-right: 10px;
    }
    .actions a:hover {
      text-decoration: underline;
    }
    .logout {
      display: inline-block;
      margin-top: 20px;
      background: red;
      color: white;
      padding: 10px 20px;
      text-decoration: none;
      border-radius: 5px;
      font-weight: bold;
    }
    .logout:hover {
      background: darkred;
    }
    .no-bookings {
      text-align: center;
      font-style: italic;
      color: #777;
    }
  </style>
</head>
<body>

<div class="container">
  <h2>Welcome, <%= (driverUsername != null) ? driverUsername : "Driver" %>!</h2>
  <p>This is your driver dashboard.</p>

  <!-- ✅ Display Success/Error Messages -->
  <%
    String message = (String) sessionDriver.getAttribute("message");
    String errorMessage = (String) sessionDriver.getAttribute("errorMessage");

    if (message != null) {
  %>
  <div class="alert alert-success"><%= message %></div>
  <%
      sessionDriver.removeAttribute("message");
    }

    if (errorMessage != null) {
  %>
  <div class="alert alert-danger"><%= errorMessage %></div>
  <%
      sessionDriver.removeAttribute("errorMessage");
    }
  %>

  <!-- ✅ Available Bookings for Drivers to Accept -->
  <h3>Available Bookings</h3>
  <table>
    <tr>
      <th>Booking ID</th>
      <th>Pickup Location</th>
      <th>Destination</th>
      <th>Status</th>
      <th>Actions</th>
    </tr>
    <%
      if (availableBookings != null && !availableBookings.isEmpty()) {
        for (Booking b : availableBookings) {
          String encodedBookingId = URLEncoder.encode(String.valueOf(b.getBookingId()), StandardCharsets.UTF_8);
    %>
    <tr>
      <td><%= b.getBookingId() %></td>
      <td><%= b.getPickupLocation() %></td>
      <td><%= b.getDestination() %></td>
      <td><%= b.getStatus() %></td>
      <td class="actions">
        <a href="DriverActionServlet?action=accept&bookingId=<%= encodedBookingId %>">✅ Accept</a>
      </td>
    </tr>
    <%
      }
    } else {
    %>
    <tr><td colspan="5" class="no-bookings">No available bookings</td></tr>
    <%
      }
    %>
  </table>

  <!-- ✅ Assigned Bookings for this Driver -->
  <h3>Your Assigned Bookings</h3>
  <table>
    <tr>
      <th>Booking ID</th>
      <th>Pickup Location</th>
      <th>Destination</th>
      <th>Status</th>
      <th>Actions</th>
    </tr>
    <%
      if (assignedBookings != null && !assignedBookings.isEmpty()) {
        for (Booking b : assignedBookings) {
          String encodedBookingId = URLEncoder.encode(String.valueOf(b.getBookingId()), StandardCharsets.UTF_8);
    %>
    <tr>

      <td><%= b.getBookingId() %></td>
      <td><%= b.getPickupLocation() %></td>
      <td><%= b.getDestination() %></td>
      <td><%= b.getStatus() %></td>

      <td class="actions">
        <% if ("Upcoming".equals(b.getStatus()) && b.getDriverId() <= 0) { %>
        <!-- Show Accept & Reject options if the booking is 'Upcoming' and unassigned -->
        <a href="DriverActionServlet?action=accept&bookingId=<%= b.getBookingId() %>">✅ Accept</a> |
        <a href="DriverActionServlet?action=reject&bookingId=<%= b.getBookingId() %>" onclick="return confirm('Are you sure you want to reject this booking?');">❌ Reject</a>

        <% } else if ("Accepted".equals(b.getStatus()) && b.getDriverId() == driverId) { %>
        <!-- Show 'Mark as Completed' if the booking is already accepted by this driver -->
        <a href="DriverActionServlet?action=complete&bookingId=<%= b.getBookingId() %>">✔️ Mark as Completed</a>

        <% } else { %>
        No Actions Available
        <% } %>
      </td>
    </tr>

    <%
      }
    } else {
    %>
    <tr><td colspan="5" class="no-bookings">No assigned bookings</td></tr>
    <%
      }
    %>
  </table>

  <a href="logout.jsp" class="logout">Logout</a>
</div>

<!-- ✅ Debugging Information -->
<p>Debug Info: Driver ID from session: <%= driverId %></p>

</body>
</html>
