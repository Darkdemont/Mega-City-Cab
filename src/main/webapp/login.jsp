<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Login</title>
</head>
<body>
<h2>Login</h2>
<% if (request.getParameter("error") != null) { %>
<p style="color: red;"><%= request.getParameter("error") %></p>
<% } %>
<form action="login" method="POST">
    <label>Username:</label>
    <input type="text" name="username" required>
    <br>
    <label>Password:</label>
    <input type="password" name="password" required>
    <br>
    <input type="submit" value="Login">
</form>
</body>
</html>
