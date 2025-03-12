package com.megacitycab.services;

import com.megacitycab.dao.BookingDAO;
import java.math.BigDecimal;

public class BillingService {
    private static final BigDecimal BASE_FARE = new BigDecimal("5.00"); // Minimum charge
    private static final BigDecimal DISTANCE_RATE = new BigDecimal("2.50"); // Per km rate
    private static final BigDecimal TAX_RATE = new BigDecimal("0.10"); // 10% Tax
    private static final BigDecimal LOYALTY_DISCOUNT = new BigDecimal("0.05"); // 5% Discount for frequent customers

    private BookingDAO bookingDAO;


    public BillingService(BookingDAO bookingDAO) {
        this.bookingDAO = bookingDAO;
    }


    public BillingService() {}


    public BigDecimal calculateTotalFare(double distanceKm) {
        return BASE_FARE.add(new BigDecimal(distanceKm).multiply(DISTANCE_RATE));
    }


    public BigDecimal calculateTax(BigDecimal totalFare) {
        return totalFare.multiply(TAX_RATE);
    }


    public BigDecimal calculateDiscount(BigDecimal totalFare, int customerId) {
        BigDecimal discountPercentage = BigDecimal.ZERO;


        if (totalFare.compareTo(new BigDecimal("50")) > 0) { // If fare > $50, apply discount
            discountPercentage = discountPercentage.add(new BigDecimal("0.10")); // 10% off
        } else if (totalFare.compareTo(new BigDecimal("25")) > 0) {
            discountPercentage = discountPercentage.add(new BigDecimal("0.05")); // 5% off
        }


        if (bookingDAO != null) {
            int rideCount = bookingDAO.getCustomerRideCount(customerId);
            if (rideCount > 10) {
                discountPercentage = discountPercentage.add(LOYALTY_DISCOUNT); // Extra 5%
            }
        }

        return totalFare.multiply(discountPercentage);
    }



    public BigDecimal calculateTotalFare(double distanceKm, int customerId) {
        BigDecimal totalFare = calculateTotalFare(distanceKm); // Compute base fare
        BigDecimal discount = calculateDiscount(totalFare, customerId); // Apply discount
        return totalFare.subtract(discount); // Return discounted fare
    }

}
