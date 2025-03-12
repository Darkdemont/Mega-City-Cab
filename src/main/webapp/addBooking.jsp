<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Add Booking</title>
</head>
<body>
<h2>Add a New Booking</h2>

<% if (request.getParameter("error") != null) { %>
<p style="color: red;"><%= request.getParameter("error") %></p>
<% } %>

<form action="bookCab" method="POST">
    <label for="customerId">Customer ID:</label>
    <input type="text" id="customerId" name="customerId" required><br>

    <label for="carId">Car ID:</label>
    <input type="text" id="carId" name="carId" required><br>

    <label for="driverId">Driver ID:</label>
    <input type="text" id="driverId" name="driverId" required><br>

    <label for="pickupLocation">Pickup Location:</label>
    <input type="text" id="pickupLocation" name="pickupLocation" required><br>

    <label for="destination">Destination:</label>
    <input type="text" id="destination" name="destination" required><br>

    <label for="distanceKm">Distance (in km):</label>
    <input type="text" id="distanceKm" name="distanceKm" required><br>

    <input type="submit" value="Book Cab">
</form>
</body>
</html>
