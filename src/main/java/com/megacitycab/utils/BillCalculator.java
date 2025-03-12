package com.megacitycab.utils;

public class BillCalculator {
    // Constants for fare calculation
    private static final double BASE_FARE = 100.0; // Fixed base fare
    private static final double RATE_PER_KM = 50.0; // Rate per kilometer
    private static final double TAX_RATE = 0.10; // 10% tax rate

    // Discount thresholds
    private static final double HIGH_FARE_DISCOUNT_10 = 0.10; // 10% discount for fare > $50
    private static final double HIGH_FARE_DISCOUNT_5 = 0.05; // 5% discount for fare > $25
    private static final double LOYALTY_DISCOUNT = 0.05; // Extra 5% for customers with >10 rides
    private static final double BASE_DISCOUNT = 0.05; // Base 5% discount for all rides

    /**
     * Calculate the total fare for a booking with automatic discounts and taxes.
     *
     * @param distanceKm The distance of the trip in kilometers.
     * @param rideCount  The number of rides the customer has completed (for loyalty discount).
     * @return The final calculated fare.
     */
    public static double calculateTotal(double distanceKm, int rideCount) {
        if (distanceKm <= 0) {
            return BASE_FARE;
        }

        double baseFare = BASE_FARE + (distanceKm * RATE_PER_KM);

        double discountPercentage = BASE_DISCOUNT;

        if (baseFare > 50) {
            discountPercentage += HIGH_FARE_DISCOUNT_10;
        } else if (baseFare > 25) {
            discountPercentage += HIGH_FARE_DISCOUNT_5;
        }

        if (rideCount > 10) {
            discountPercentage += LOYALTY_DISCOUNT;
        }

        double discountAmount = baseFare * discountPercentage;
        double taxAmount = baseFare * TAX_RATE;

        double finalFare = baseFare + taxAmount - discountAmount;

        return Math.max(finalFare, BASE_FARE);
    }
}
