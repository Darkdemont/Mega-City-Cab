<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="java.sql.*, java.math.BigDecimal" %>
<%@ page import="com.megacitycab.dao.DatabaseConnection, com.megacitycab.models.Payment" %>

<%

    String bookingId = request.getParameter("booking_id");
    if (bookingId == null || bookingId.isEmpty()) {
        out.println("<p style='color:red;'>❌ Invalid booking ID.</p>");
        return;
    }

    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    BigDecimal totalFare = BigDecimal.ZERO;
    BigDecimal tax = BigDecimal.ZERO;
    BigDecimal discount = BigDecimal.ZERO;
    BigDecimal finalAmount = BigDecimal.ZERO;
    String paymentStatus = "Pending";

    try {

        conn = DatabaseConnection.getConnection();


        String query = "SELECT amount_paid, payment_status FROM payments WHERE booking_id = ?";
        stmt = conn.prepareStatement(query);
        stmt.setInt(1, Integer.parseInt(bookingId));
        rs = stmt.executeQuery();

        if (rs.next()) {
            totalFare = rs.getBigDecimal("amount_paid");
            paymentStatus = rs.getString("payment_status");


            tax = totalFare.multiply(new BigDecimal("0.10"));  // 10% tax
            discount = totalFare.multiply(new BigDecimal("0.05"));  // 5% discount
            finalAmount = totalFare.add(tax).subtract(discount);
        } else {
            out.println("<p style='color:red;'>❌ No payment record found for this booking.</p>");
            return;
        }
    } catch (Exception e) {
        e.printStackTrace();
        out.println("<p style='color:red;'>❌ Error loading payment details.</p>");
        return;
    } finally {

        if (rs != null) try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
        if (stmt != null) try { stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        if (conn != null) try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
    }
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Payment</title>
    <script>
        function printInvoice() {
            window.print();
        }
    </script>
</head>
<body>
<h2>Payment for Booking ID: <%= bookingId %></h2>


<p><strong>Total Fare:</strong> $<%= totalFare %></p>
<p><strong>Tax (10%):</strong> $<%= tax %></p>
<p><strong>Discount Applied (5%):</strong> $<%= discount %></p>
<p><strong>Final Amount:</strong> $<%= finalAmount %></p>


<p><strong>Payment Status: </strong> <%= paymentStatus %></p>


<% if ("Pending".equals(paymentStatus)) { %>
<form action="PayBillServlet" method="POST">
    <input type="hidden" name="bookingId" value="<%= bookingId %>">
    <input type="hidden" name="finalAmount" value="<%= finalAmount %>">
    <button type="submit">Pay Now</button>
</form>
<% } else { %>
<h3 style="color:green;">✅ Payment Completed!</h3>
<% } %>


<button type="button" onclick="printInvoice()">Print Invoice</button>

</body>
</html>
