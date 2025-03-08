<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sign Up | MegaCityCab</title>
    <link rel="stylesheet" href="assets/css/style.css">
    <script defer src="assets/js/script.js"></script>
</head>
<body>
<div class="container">
    <div class="signup-box">
        <h2>Sign Up</h2>

        <%-- Display error message if signup fails --%>
        <% String error = request.getParameter("error"); %>
        <% if (error != null) { %>
        <p class="error-message"><%= error %></p>
        <% } %>

        <%-- Display success message if signup is successful --%>
        <% String success = request.getParameter("success"); %>
        <% if (success != null) { %>
        <p class="success-message"><%= success %></p>
        <% } %>

        <form action="SignupServlet" method="post" id="signup-form">
            <label for="username">Username</label>
            <input type="text" id="username" name="username" required>
            <p id="username-message" class="error-message" style="display: none;"></p>

            <label for="email">Email</label>
            <input type="email" id="email" name="email" required>
            <p id="email-message" class="error-message" style="display: none;"></p>

            <label for="password">Password</label>
            <input type="password" id="password" name="password" required>
            <p id="password-message" class="error-message" style="display: none;"></p>

            <button type="submit" class="btn">Register</button>
        </form>

        <p>Already have an account? <a href="login.jsp" class="login-link">Login here</a></p>
    </div>
</div>
</body>
</html>
