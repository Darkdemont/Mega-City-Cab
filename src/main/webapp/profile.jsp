<%@ page import="com.megacitycab.models.User" %>
<%@ page import="jakarta.servlet.http.HttpSession" %>
<%@ page session="true" %>

<%
  HttpSession userSession = request.getSession(false);
  User customer = (userSession != null) ? (User) userSession.getAttribute("loggedInUser") : null;
  if (customer == null) {
    response.sendRedirect("index.jsp");
    return;
  }

  String message = (String) session.getAttribute("message");
  String errorMessage = (String) session.getAttribute("errorMessage");


  if (message != null) {
    session.removeAttribute("message");
  }
  if (errorMessage != null) {
    session.removeAttribute("errorMessage");
  }
%>

<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Update Your Profile</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="container mt-4">

<h2 class="text-center text-primary">Update Your Profile</h2>
<p class="text-center text-muted">Please provide your NIC, Mobile Number, and Address.</p>
<hr>


<% if (message != null) { %>
<div class="alert alert-success text-center"><%= message %></div>
<% } %>


<% if (errorMessage != null) { %>
<div class="alert alert-danger text-center"><%= errorMessage %></div>
<% } %>

<form action="ProfileUpdateServlet" method="POST" class="card p-4 shadow-sm">
  <div class="mb-3">
    <label class="form-label">Username</label>
    <input type="text" class="form-control" value="<%= customer.getUsername() %>" readonly>
  </div>
  <div class="mb-3">
    <label class="form-label">Email</label>
    <input type="email" class="form-control" value="<%= customer.getEmail() %>" readonly>
  </div>
  <div class="mb-3">
    <label class="form-label">Mobile Number</label>
    <input type="text" name="mobile" class="form-control" value="<%= customer.getMobile() != null ? customer.getMobile() : "" %>" required>
  </div>
  <div class="mb-3">
    <label class="form-label">NIC</label>
    <input type="text" name="nic" class="form-control" value="<%= customer.getNic() != null ? customer.getNic() : "" %>" required>
  </div>
  <div class="mb-3">
    <label class="form-label">Address</label>
    <input type="text" name="address" class="form-control" value="<%= customer.getAddress() != null ? customer.getAddress() : "" %>" required>
  </div>
  <button type="submit" class="btn btn-primary w-100">Save Details</button>
</form>

<a href="BookingServlet" class="btn btn-secondary mt-3">Back to Dashboard</a>

</body>
</html>
