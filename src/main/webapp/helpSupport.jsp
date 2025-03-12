<%@ page import="jakarta.servlet.http.HttpSession" %>
<%@ page import="java.util.List" %>
<%@ page import="com.megacitycab.models.SupportTicket" %>
<%@ page session="true" %>

<%
  HttpSession userSession = request.getSession(false);
  if (userSession == null || userSession.getAttribute("loggedInUser") == null) {
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


  List<SupportTicket> userTickets = (List<SupportTicket>) request.getAttribute("userTickets");
%>

<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Help & Support</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="container mt-4">

<h2 class="text-center text-primary">Help & Support</h2>
<p class="text-center text-muted">Find answers to common issues or submit a complaint.</p>
<hr>


<% if (message != null) { %>
<div class="alert alert-success text-center"><%= message %></div>
<% } %>


<% if (errorMessage != null) { %>
<div class="alert alert-danger text-center"><%= errorMessage %></div>
<% } %>


<h3 class="text-primary">Frequently Asked Questions (FAQs)</h3>
<div class="accordion" id="faqAccordion">
  <div class="accordion-item">
    <h2 class="accordion-header" id="faq1">
      <button class="accordion-button" type="button" data-bs-toggle="collapse" data-bs-target="#collapseOne">
        How can I update my profile information?
      </button>
    </h2>
    <div id="collapseOne" class="accordion-collapse collapse show" data-bs-parent="#faqAccordion">
      <div class="accordion-body">
        You can update your profile by visiting the "Profile" section in your dashboard and editing the details.
      </div>
    </div>
  </div>

  <div class="accordion-item">
    <h2 class="accordion-header" id="faq2">
      <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#collapseTwo">
        What should I do if I forgot my password?
      </button>
    </h2>
    <div id="collapseTwo" class="accordion-collapse collapse" data-bs-parent="#faqAccordion">
      <div class="accordion-body">
        Click on "Forgot Password" on the login page and follow the instructions to reset your password.
      </div>
    </div>
  </div>
</div>
<hr>


<h3 class="text-primary">Submit a Complaint</h3>
<form action="SupportTicketServlet" method="POST" class="card p-4 shadow-sm">
  <div class="mb-3">
    <label class="form-label">Subject</label>
    <input type="text" name="subject" class="form-control" required>
  </div>
  <div class="mb-3">
    <label class="form-label">Describe Your Issue</label>
    <textarea name="description" class="form-control" rows="4" required></textarea>
  </div>
  <button type="submit" class="btn btn-primary w-100">Submit Complaint</button>
</form>

<hr>


<h3 class="text-primary">Your Submitted Complaints</h3>
<table class="table table-bordered table-striped">
  <thead class="table-dark">
  <tr>
    <th>Ticket ID</th>
    <th>Subject</th>
    <th>Description</th>
    <th>Status</th>
  </tr>
  </thead>
  <tbody>
  <% if (userTickets != null && !userTickets.isEmpty()) {
    for (SupportTicket ticket : userTickets) { %>
  <tr>
    <td><%= ticket.getTicketId() %></td>
    <td><%= ticket.getSubject() %></td>
    <td><%= ticket.getDescription() %></td>
    <td>
      <% if ("Open".equals(ticket.getStatus())) { %>
      <span class="badge bg-warning text-dark">Open</span>
      <% } else if ("In Progress".equals(ticket.getStatus())) { %>
      <span class="badge bg-primary">In Progress</span>
      <% } else if ("Resolved".equals(ticket.getStatus())) { %>
      <span class="badge bg-success">Resolved</span>
      <% } else if ("Closed".equals(ticket.getStatus())) { %>
      <span class="badge bg-secondary">Closed</span>
      <% } else { %>
      <span class="badge bg-dark">Unknown</span>
      <% } %>
    </td>
  </tr>
  <% } } else { %>
  <tr>
    <td colspan="4" class="text-center text-muted">No complaints submitted yet.</td>
  </tr>
  <% } %>
  </tbody>
</table>

<a href="customerIndex.jsp" class="btn btn-secondary mt-3">Back to Dashboard</a>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
