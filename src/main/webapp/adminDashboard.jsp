<%@ page import="java.util.List" %>
<%@ page import="com.megacitycab.models.Booking" %>
<%@ page import="com.megacitycab.models.User" %>
<%@ page import="com.megacitycab.models.SupportTicket" %>
<link rel="stylesheet" href="assets/css/admin.css">
<script defer src="assets/js/admin.js"></script>
<%@ page contentType="text/html; charset=UTF-8" language="java" %>

<%
  String message = (String) session.getAttribute("message");
  String errorMessage = (String) session.getAttribute("errorMessage");


  String action = request.getParameter("action");
  if (action == null) {
    action = "manageBookings";
  }
%>

<!DOCTYPE html>
<html lang="en">
<head>

  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Admin Dashboard</title>


  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
</head>
<body class="container mt-4">

<h2>Admin Dashboard</h2>
<a href="LogoutServlet" class="btn btn-danger">Logout</a>
<hr>


<% if (message != null) { %>
<div class="alert alert-success"><%= message %></div>
<% session.removeAttribute("message"); %>
<% } %>
<% if (errorMessage != null) { %>
<div class="alert alert-danger"><%= errorMessage %></div>
<% session.removeAttribute("errorMessage"); %>
<% } %>


<ul class="nav nav-tabs">
  <li class="nav-item">
    <a class="nav-link <%= "manageBookings".equals(action) ? "active" : "" %>" href="AdminServlet?action=manageBookings">📅 Manage Bookings</a>
  </li>
  <li class="nav-item">
    <a class="nav-link <%= "manageUsers".equals(action) ? "active" : "" %>" href="AdminServlet?action=manageUsers">👤 Manage Users</a>
  </li>
  <li class="nav-item">
    <a class="nav-link <%= "generateReports".equals(action) ? "active" : "" %>" href="AdminServlet?action=generateReports">📊 Generate Reports</a>
  </li>
  <li class="nav-item">
    <a class="nav-link <%= "manageSupportTickets".equals(action) ? "active" : "" %>"
       href="AdminSupportServlet?action=manageSupportTickets">📩 Support Tickets</a>
  </li>


</ul>

<div class="tab-content mt-3">

  <div class="tab-pane fade <%= "manageBookings".equals(action) ? "show active" : "" %>" id="bookings">
    <h3>Manage Bookings</h3>


    <%
      List<Booking> bookings = (List<Booking>) request.getAttribute("bookings");
      if (bookings == null) { %>
    <div class="alert alert-danger">❌ Error: Bookings data is NULL!</div>
    <% } else { %>
    <div class="alert alert-success">✅ Retrieved <strong><%= bookings.size() %></strong> bookings from servlet.</div>
    <% } %>

    <table class="table table-striped">
      <thead>
      <tr>
        <th>Booking ID</th>
        <th>Customer ID</th>
        <th>Pickup</th>
        <th>Destination</th>
        <th>Status</th>
        <th>Driver</th>
        <th>Action</th>
      </tr>
      </thead>
      <tbody>
      <% if (bookings != null && !bookings.isEmpty()) {
        for (Booking booking : bookings) { %>
      <tr>
        <td><%= booking.getBookingId() %></td>
        <td><%= booking.getCustomerId() %></td>
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
          <form action="AdminServlet" method="POST">
            <input type="hidden" name="action" value="cancelBooking">
            <input type="hidden" name="bookingId" value="<%= booking.getBookingId() %>">
            <button type="submit" class="btn btn-danger btn-sm">Cancel</button>
          </form>
          <% } %>
        </td>
      </tr>
      <% } } else { %>
      <tr><td colspan="7" class="text-center text-muted">No bookings found.</td></tr>
      <% } %>
      </tbody>
    </table>
  </div>



    <div class="tab-pane fade <%= "manageSupportTickets".equals(action) ? "show active" : "" %>" id="supportTickets">
      <h3>Manage Support Tickets</h3>


      <%
        List<SupportTicket> tickets = (List<SupportTicket>) request.getAttribute("supportTickets");
        if (tickets == null) { %>
      <div class="alert alert-danger">❌ Error: Support tickets data is NULL!</div>
      <% } else { %>
      <div class="alert alert-success">✅ Retrieved <strong><%= tickets.size() %></strong> support tickets from servlet.</div>
      <% } %>

      <table class="table table-striped">
        <thead>
        <tr>
          <th>Ticket ID</th>
          <th>User ID</th>
          <th>Subject</th>
          <th>Description</th>
          <th>Status</th>
          <th>Action</th>
        </tr>
        </thead>
        <tbody>
        <% if (tickets != null && !tickets.isEmpty()) {
          for (SupportTicket ticket : tickets) { %>
        <tr>
          <td><%= ticket.getTicketId() %></td>
          <td><%= ticket.getUserId() %></td>
          <td><%= ticket.getSubject() %></td>
          <td><%= ticket.getDescription() %></td>
          <td>
            <form action="AdminSupportServlet" method="POST">
              <input type="hidden" name="action" value="updateTicketStatus">
              <input type="hidden" name="ticketId" value="<%= ticket.getTicketId() %>">
              <select name="status" class="form-select">
                <option value="Open" <%= ticket.getStatus().equals("Open") ? "selected" : "" %>>Open</option>
                <option value="In Progress" <%= ticket.getStatus().equals("In Progress") ? "selected" : "" %>>In Progress</option>
                <option value="Resolved" <%= ticket.getStatus().equals("Resolved") ? "selected" : "" %>>Resolved</option>
                <option value="Closed" <%= ticket.getStatus().equals("Closed") ? "selected" : "" %>>Closed</option>
              </select>
              <button type="submit" class="btn btn-primary btn-sm">Update</button>
            </form>
          </td>
        </tr>
        <% } } else { %>
        <tr><td colspan="6" class="text-center text-muted">No support tickets found.</td></tr>
        <% } %>
        </tbody>
      </table>
    </div>




  <div class="tab-pane fade <%= "manageUsers".equals(action) ? "show active" : "" %>" id="users">
    <h3>Manage Users</h3>


    <%
      List<User> users = (List<User>) request.getAttribute("users");
      if (users == null) { %>
    <div class="alert alert-danger">❌ Error: Users data is NULL!</div>
    <% } else { %>
    <div class="alert alert-success">✅ Retrieved <strong><%= users.size() %></strong> users from servlet.</div>
    <% } %>

    <table class="table table-striped">
      <thead>
      <tr>
        <th>User ID</th>
        <th>Username</th>
        <th>Email</th>
        <th>Role</th>
        <th>Actions</th>
      </tr>
      </thead>
      <tbody>
      <% if (users != null && !users.isEmpty()) {
        for (User user : users) { %>
      <tr>
        <td><%= user.getUserId() %></td>
        <td><%= user.getUsername() %></td>
        <td><%= user.getEmail() %></td>
        <td><%= user.getRole() %></td>
        <td>
          <form action="AdminServlet" method="POST">
            <input type="hidden" name="action" value="resetPassword">
            <input type="hidden" name="userId" value="<%= user.getUserId() %>">
            <button type="submit" class="btn btn-warning btn-sm">Reset Password</button>
          </form>
          <form action="AdminServlet" method="POST">
            <input type="hidden" name="action" value="deleteUser">
            <input type="hidden" name="userId" value="<%= user.getUserId() %>">
            <button type="submit" class="btn btn-danger btn-sm">Delete</button>
          </form>
        </td>
      </tr>
      <% } } else { %>
      <tr><td colspan="5" class="text-center text-muted">No users found.</td></tr>
      <% } %>
      </tbody>
    </table>
  </div>


  <div class="tab-pane fade <%= "generateReports".equals(action) ? "show active" : "" %>" id="reports">
    <h3>Reports</h3>
    <p>Total Earnings: <strong>$<%= request.getAttribute("totalEarnings") %></strong></p>
    <p>Total Bookings: <strong><%= request.getAttribute("totalBookings") %></strong></p>
    <p>Total Customers: <strong><%= request.getAttribute("totalCustomers") %></strong></p>
  </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>



