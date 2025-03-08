<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login | MegaCityCab</title>
    <link rel="stylesheet" href="assets/css/login.css">
    <script defer src="assets/js/login.js"></script>
</head>
<body>
<div class="container">
    <div class="login-box">
        <h2>Login</h2>

        <%-- Display error message if login fails --%>
        <% String error = request.getParameter("error"); %>
        <% if (error != null) { %>
        <p class="error-message"><%= error %></p>
        <% } %>

        <%-- Display success message if login is successful --%>
        <% String success = request.getParameter("success"); %>
        <% if (success != null) { %>
        <p class="success-message"><%= success %></p>
        <% } %>

        <form id="login-form" action="login" method="POST">
            <label for="username">Username:</label>
            <input type="text" id="username" name="username" required placeholder="Enter your username">
            <p id="username-message" class="error-message hidden"></p>

            <label for="password">Password:</label>
            <div class="password-container">
                <input type="password" id="password" name="password" required placeholder="Enter your password">
                <span id="togglePassword">👁️</span>
            </div>
            <p id="password-message" class="error-message hidden"></p>

            <button type="submit" class="btn">Login</button>
        </form>

        <p>Don't have an account? <a href="signup.jsp" class="signup-link">Sign up here</a></p>
    </div>
</div>
</body>
</html>