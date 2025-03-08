<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="com.megacitycab.models.Booking" %>

<!DOCTYPE html>
<html>
<head>
  <title>Calculate Bill</title>
</head>
<body>
<h2>Calculate Bill</h2>

<% if (request.getParameter("error") != null) { %>
<p style="color: red;"><%= request.getParameter("error") %></p>
<% } %>

<form action="calculateBill" method="POST">
  <label for="bookingId">Booking ID:</label>
  <input type="text" id="bookingId" name="bookingId" required>
  <br>

  <label for="discount">Discount (in currency):</label>
  <input type="text" id="discount" name="discount" value="0" required>
  <br>

  <input type="submit" value="Calculate Bill">
</form>

<% Booking booking = (Booking) request.getAttribute("booking"); %>
<% Double totalBill = (Double) request.getAttribute("totalBill"); %>
<% if (booking != null && totalBill != null) { %>
<h3>Booking Details:</h3>
<p><b>Pickup Location:</b> <%= booking.getPickupLocation() %></p>
<p><b>Destination:</b> <%= booking.getDestination() %></p>
<p><b>Distance:</b> <%= booking.getDistanceKm() %> km</p>
<p><b>Total Bill:</b> $<%= totalBill %></p>
<% } %>
</body>
</html>
