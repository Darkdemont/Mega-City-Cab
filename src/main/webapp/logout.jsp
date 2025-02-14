<%@ page import="jakarta.servlet.http.HttpSession" %>
<%
  HttpSession sessionLogout = request.getSession(false);
  if (sessionLogout != null) {
    sessionLogout.invalidate(); // Destroy session
  }
  response.sendRedirect("login.jsp"); // Redirect to login page
%>
