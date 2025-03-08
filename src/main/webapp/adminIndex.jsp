<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="jakarta.servlet.http.HttpSession" %>
<%
    HttpSession sessionAdmin = request.getSession(false);
    if (sessionAdmin == null || !"Admin".equals(sessionAdmin.getAttribute("role"))) {
        response.sendRedirect("login.jsp?error=Unauthorized Access");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title>Admin Dashboard</title>
</head>
<body>
<h2>Welcome, Admin <%= sessionAdmin.getAttribute("username") %>!</h2>
<p>This is your admin dashboard.</p>
<a href="logout.jsp">Logout</a>
</body>
</html>
