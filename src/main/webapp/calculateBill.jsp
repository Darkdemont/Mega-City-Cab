<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="java.sql.*, java.math.BigDecimal" %>
<%@ page import="com.megacitycab.dao.DatabaseConnection, com.megacitycab.models.Payment" %>

<%
  String bookingId = request.getParameter("booking_id");
  if (bookingId == null || bookingId.isEmpty()) {
    out.println("<p style='color:red;'>❌ Invalid booking ID.</p>");
    return;
  }

  Connection conn = DatabaseConnection.getConnection();
  PreparedStatement stmt = null;
  ResultSet rs = null;
  Payment payment = null;
  BigDecimal totalFare = BigDecimal.ZERO;
  BigDecimal tax = BigDecimal.ZERO;
  BigDecimal discount = BigDecimal.ZERO;
  BigDecimal finalAmount = BigDecimal.ZERO;

  try {

    String query = "SELECT amount_paid, payment_status FROM payments WHERE booking_id = ?";
    stmt = conn.prepareStatement(query);
    stmt.setInt(1, Integer.parseInt(bookingId));
    rs = stmt.executeQuery();

    if (rs.next()) {
      totalFare = rs.getBigDecimal("amount_paid");
      tax = totalFare.multiply(new BigDecimal("0.10")); // 10% tax
      discount = totalFare.multiply(new BigDecimal("0.05")); // Assume 5% discount applied
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
    if (stmt != null) stmt.close();
    if (rs != null) rs.close();
    conn.close();
  }
%>

<!DOCTYPE html>
<html>
<head>
  <title>Ride Invoice</title>
  <script>
    function printInvoice() {
      window.print();
    }
  </script>
</head>
<body>
<h2>Ride Invoice</h2>

<form action="calculateBill" method="POST">
  <label for="bookingId">Booking ID:</label>
  <input type="text" id="bookingId" name="bookingId" value="<%= bookingId %>" readonly>
  <br>

  <label for="totalFare">Total Fare:</label>
  <input type="text" id="totalFare" name="totalFare" value="$<%= totalFare %>" readonly>
  <br>

  <label for="tax">Tax (10%):</label>
  <input type="text" id="tax" name="tax" value="$<%= tax %>" readonly>
  <br>

  <label for="discount">Discount Applied:</label>
  <input type="text" id="discount" name="discount" value="$<%= discount %>" readonly>
  <br>

  <label for="netAmount">Final Amount:</label>
  <input type="text" id="netAmount" name="netAmount" value="$<%= finalAmount %>" readonly>
  <br>
</form>


<form action="DownloadInvoiceServlet" method="GET">
  <input type="hidden" name="bookingId" value="<%= bookingId %>">
  <button type="submit">Download Invoice as PDF</button>
</form>


<form action="PayBillServlet" method="POST">
  <input type="hidden" name="bookingId" value="<%= bookingId %>">
  <input type="hidden" name="finalAmount" value="<%= finalAmount %>">
  <button type="submit">Pay Now</button>
</form>



<% if (request.getParameter("success") != null) { %>
<h3 style="color:green;">✅ Payment Recorded! Your total bill is: $<%= finalAmount %></h3>
<% } else if (request.getParameter("error") != null) { %>
<h3 style="color:red;">❌ Error processing payment. Try again.</h3>
<% } %>

</body>
</html>