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
  <!-- Google Maps API -->
  <script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyAC25mXxrVn9pTIFZTrH8TokvdZYwZHq9I&libraries=places&callback=initMap" async defer></script>
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
    <a class="navbar-brand" href="index.jsp">MegaCityCab</a>
    <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
      <span class="navbar-toggler-icon"></span>
    </button>
    <div class="collapse navbar-collapse" id="navbarNav">
      <ul class="navbar-nav ms-auto">
        <li class="nav-item"><a class="nav-link" href="index.jsp">Home</a></li>
        <li class="nav-item"><a class="nav-link" href="about.jsp">About</a></li>
        <li class="nav-item"><a class="nav-link" href="services.jsp">Services</a></li>
        <li class="nav-item"><a class="nav-link" href="manageBookings.jsp">Manage Bookings</a></li>
        <li class="nav-item"><a class="nav-link" href="helpSupport.jsp">Help & Support</a></li>
        <li class="nav-item"><a class="nav-link" href="contact.jsp">Contact</a></li>
        <li class="nav-item"><a class="nav-link" href="profile.jsp">Profile</a></li>
        <li class="nav-item"><span class="nav-link text-warning">Welcome, <%= customer.getUsername() %>!</span></li>
        <li class="nav-item"><a class="nav-link btn btn-danger text-white ms-2" href="LogoutServlet">Logout</a></li>
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
<% session.removeAttribute("message"); %>
<% } if (errorMessage != null) { %>
<div class="alert alert-danger"><%= errorMessage %></div>
<% session.removeAttribute("errorMessage"); %>
<% } %>

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
          <% List<Car> availableCars = (List<Car>) request.getAttribute("availableCars");
            if (availableCars != null && !availableCars.isEmpty()) {
              for (Car car : availableCars) { %>
          <option value="<%= car.getCarModel() %>"><%= car.getCarModel() %></option>
          <% } } else { %>
          <option value="" disabled>No Cars Available</option>
          <% } %>
        </select>
      </div>
      <div class="col-md-4">
        <label class="form-label">Date & Time</label>
        <input type="datetime-local" name="dateTime" id="dateTime" class="form-control" required>
      </div>
      <div class="col-md-4">
        <label class="form-label">Distance (km)</label>
        <input type="number" name="distanceKm" id="distanceKm" class="form-control" placeholder="Auto-calculated" min="1" readonly>
      </div>
      <div class="col-md-12 mt-3">
        <div id="map" style="height: 300px; width: 100%; border-radius: 10px;"></div>
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
  <% List<Booking> bookings = (List<Booking>) request.getAttribute("bookings");
    if (bookings != null && !bookings.isEmpty()) {
      for (Booking booking : bookings) { %>
  <tr>
    <td><%= booking.getBookingId() %></td>
    <td><%= booking.getPickupLocation() %></td>
    <td><%= booking.getDestination() %></td>
    <td><%= booking.getStatus() %></td>
    <td><% if (booking.getDriverId() > 0) { %>Driver ID: <%= booking.getDriverId() %>
      <% } else { %><span class="text-muted">No driver assigned</span><% } %></td>
    <td><% if ("Upcoming".equals(booking.getStatus())) { %>
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
  <% } } else { %>
  <tr><td colspan="6" class="text-center text-muted">No bookings found.</td></tr>
  <% } %>
  </tbody>
</table>

<hr>

<div class="row">
  <div class="col-md-6">
    <div class="card shadow-sm p-3">
      <h4 class="text-primary">Available Cars</h4>
      <ul class="list-group">
        <% if (availableCars != null && !availableCars.isEmpty()) {
          for (Car car : availableCars) { %>
        <li class="list-group-item d-flex justify-content-between align-items-center">
          <span><strong><%= car.getCarModel() %></strong> - <%= car.getCarStatus() %></span>
          <span class="badge bg-success">Available</span>
        </li>
        <% } } else { %>
        <li class="list-group-item text-muted text-center">No cars available.</li>
        <% } %>
      </ul>
    </div>
  </div>
  <div class="col-md-6">
    <div class="card shadow-sm p-3">
      <h4 class="text-success">Available Drivers</h4>
      <ul class="list-group">
        <% List<Driver> availableDrivers = (List<Driver>) request.getAttribute("availableDrivers");
          if (availableDrivers != null && !availableDrivers.isEmpty()) {
            for (Driver driver : availableDrivers) { %>
        <li class="list-group-item d-flex justify-content-between align-items-center">
          <span><strong><%= driver.getDriverName() %></strong> - <%= driver.getDriverStatus() %></span>
          <span class="badge bg-primary">Online</span>
        </li>
        <% } } else { %>
        <li class="list-group-item text-muted text-center">No drivers available.</li>
        <% } %>
      </ul>
    </div>
  </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
  let map, pickupMarker, destMarker, directionsService, directionsRenderer;
  let pickupAutocomplete, destAutocomplete;

  // Initialize Google Map
  function initMap() {
    try {
      console.log("Initializing Google Map...");
      map = new google.maps.Map(document.getElementById("map"), {
        center: { lat: 6.9271, lng: 79.8612 }, // Default: Colombo, Sri Lanka
        zoom: 12,
      });
      console.log("Map initialized successfully");

      directionsService = new google.maps.DirectionsService();
      directionsRenderer = new google.maps.DirectionsRenderer({
        map: map,
        suppressMarkers: true, // Use custom markers
      });
      console.log("Directions Service and Renderer initialized");

      const pickupInput = document.getElementById("pickupLocation");
      pickupAutocomplete = new google.maps.places.Autocomplete(pickupInput, {
        componentRestrictions: { country: "lk" },
        fields: ["place_id", "formatted_address", "geometry"],
      });
      pickupAutocomplete.bindTo("bounds", map);
      console.log("Pickup Autocomplete initialized");

      const destInput = document.getElementById("destination");
      destAutocomplete = new google.maps.places.Autocomplete(destInput, {
        componentRestrictions: { country: "lk" },
        fields: ["place_id", "formatted_address", "geometry"],
      });
      destAutocomplete.bindTo("bounds", map);
      console.log("Destination Autocomplete initialized");

      pickupAutocomplete.addListener("place_changed", () => {
        console.log("Pickup location changed");
        updateMapAndRoute();
      });

      destAutocomplete.addListener("place_changed", () => {
        console.log("Destination changed");
        updateMapAndRoute();
      });

    } catch (error) {
      console.error("Map initialization error:", error.message, error.stack);
      alert("Failed to load Google Maps. Check console for details.");
    }
  }

  // Update Map, Calculate Distance, and Display Route
  function updateMapAndRoute() {
    try {
      console.log("Updating map and route...");
      const pickupPlace = pickupAutocomplete.getPlace();
      const destPlace = destAutocomplete.getPlace();

      if (!pickupPlace || !pickupPlace.geometry) {
        console.warn("Invalid pickup place or no geometry:", pickupPlace);
        clearMap();
        return;
      }
      if (!destPlace || !destPlace.geometry) {
        console.warn("Invalid destination place or no geometry:", destPlace);
        clearMap();
        return;
      }

      const pickupLatLng = pickupPlace.geometry.location;
      const destLatLng = destPlace.geometry.location;
      const pickupAddress = pickupPlace.formatted_address;
      const destAddress = destPlace.formatted_address;

      console.log("Pickup Address:", pickupAddress, "LatLng:", pickupLatLng.toString());
      console.log("Destination Address:", destAddress, "LatLng:", destLatLng.toString());

      addMarkers(pickupLatLng, destLatLng);
      calculateAndDisplayRoute(pickupAddress, destAddress);
    } catch (error) {
      console.error("Error in updateMapAndRoute:", error.message, error.stack);
      alert("Failed to update map and route. Check console for details.");
    }
  }

  // Calculate and Display Route
  function calculateAndDisplayRoute(origin, destination) {
    try {
      console.log("Calculating route from", origin, "to", destination);
      directionsService.route(
              {
                origin: origin,
                destination: destination,
                travelMode: "DRIVING",
              },
              (response, status) => {
                if (status === "OK") {
                  directionsRenderer.setDirections(response);
                  const bounds = response.routes[0].bounds;
                  map.fitBounds(bounds);

                  const distance = response.routes[0].legs[0].distance;
                  if (distance) {
                    const distanceKm = (distance.value / 1000).toFixed(2);
                    document.getElementById("distanceKm").value = distanceKm;
                    console.log("Distance calculated:", distanceKm, "km");
                  } else {
                    console.warn("Distance not found in response");
                    document.getElementById("distanceKm").value = "N/A";
                  }
                } else {
                  console.error("Directions API Error:", status, "for", origin, "to", destination);
                  alert("Error displaying route: " + status);
                  document.getElementById("distanceKm").value = "N/A";
                }
              }
      );
    } catch (error) {
      console.error("Error in calculateAndDisplayRoute:", error.message, error.stack);
      alert("Failed to calculate route. Check console for details.");
    }
  }

  // Add Markers to Map
  function addMarkers(pickupLatLng, destLatLng) {
    try {
      console.log("Adding markers...");
      if (pickupMarker) pickupMarker.setMap(null);
      if (destMarker) destMarker.setMap(null);

      pickupMarker = new google.maps.Marker({
        position: pickupLatLng,
        map: map,
        label: "P",
        icon: { url: "http://maps.google.com/mapfiles/ms/icons/green-dot.png" },
      });

      destMarker = new google.maps.Marker({
        position: destLatLng,
        map: map,
        label: "D",
        icon: { url: "http://maps.google.com/mapfiles/ms/icons/red-dot.png" },
      });

      const bounds = new google.maps.LatLngBounds();
      bounds.extend(pickupLatLng);
      bounds.extend(destLatLng);
      map.fitBounds(bounds);
      console.log("Markers added successfully");
    } catch (error) {
      console.error("Error in addMarkers:", error.message, error.stack);
      alert("Failed to add markers. Check console for details.");
    }
  }

  // Clear Map
  function clearMap() {
    try {
      console.log("Clearing map...");
      if (directionsRenderer) directionsRenderer.setDirections({ routes: [] });
      if (pickupMarker) pickupMarker.setMap(null);
      if (destMarker) destMarker.setMap(null);
      document.getElementById("distanceKm").value = "";
    } catch (error) {
      console.error("Error in clearMap:", error.message, error.stack);
    }
  }

  // Ensure map loads
  window.initMap = initMap;
</script>
</body>
</html>