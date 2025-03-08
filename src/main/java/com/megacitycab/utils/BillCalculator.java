package com.megacitycab.utils;

public class BillCalculator {
    // Constants for fare calculation
    private static final double BASE_FARE = 100.0; // Fixed base fare
    private static final double RATE_PER_KM = 50.0; // Rate per kilometer
    private static final double TAX_RATE = 0.10; // 10% tax rate

    /**
     * Calculate the total fare for a booking.
     *
     * @param distanceKm The distance of the trip in kilometers.
     * @param discount   The discount to apply to the fare (in currency).
     * @return The total calculated fare, ensuring it is not negative.
     */
    public static double calculateTotal(double distanceKm, double discount) {
        // Ensure distance is valid
        if (distanceKm <= 0) {
            return BASE_FARE; // Minimum fare applies if distance is 0 or invalid
        }

        // Calculate base fare based on distance
        double baseFare = BASE_FARE + (distanceKm * RATE_PER_KM);

        // Calculate tax on the base fare
        double tax = baseFare * TAX_RATE;

        // Calculate the total fare
        double total = baseFare + tax - discount;

        // Ensure the total is not negative (minimum fare is the base fare)
        return Math.max(total, BASE_FARE);
    }
}
