<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Sign Up</title>
    <link rel="stylesheet" type="text/css" href="assets/css/style.css">
</head>
<body>
<h2>Sign Up</h2>

<%-- Display error message if signup fails --%>
<% String error = request.getParameter("error"); %>
<% if (error != null) { %>
<p style="color:red;"><%= error %></p>
<% } %>

<%-- Display success message if signup is successful --%>
<% String success = request.getParameter("success"); %>
<% if (success != null) { %>
<p style="color:green;"><%= success %></p>
<% } %>

<form action="SignupServlet" method="post">
    <label for="username">Username:</label>
    <input type="text" id="username" name="username" required>

    <label for="email">Email:</label>
    <input type="email" id="email" name="email" required>

    <label for="password">Password:</label>
    <input type="password" id="password" name="password" required>

    <button type="submit">Register</button>
</form>

<p>Already have an account? <a href="login.jsp">Login here</a></p>
</body>
</html>
