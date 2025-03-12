package com.megacitycab.models;

import java.math.BigDecimal;

public class Payment {
    private int bookingId;
    private int customerId;
    private BigDecimal amountPaid;
    private String paymentMethod;
    private String paymentStatus;

    public Payment(int bookingId, int customerId, BigDecimal amountPaid, String paymentMethod, String paymentStatus) {
        this.bookingId = bookingId;
        this.customerId = customerId;
        this.amountPaid = amountPaid;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
    }

    public int getBookingId() { return bookingId; }
    public int getCustomerId() { return customerId; }
    public BigDecimal getAmountPaid() { return amountPaid; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getPaymentStatus() { return paymentStatus; }
}
