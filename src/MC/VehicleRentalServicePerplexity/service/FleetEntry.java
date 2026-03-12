package MC.VehicleRentalServicePerplexity.service;

import MC.VehicleRentalServicePerplexity.model.VehicleType;

/**
 * Simple value object used when onboarding a branch.
 * Encapsulates vehicle type, count, and price in a readable, builder-free DTO.
 */
public final class FleetEntry {

    public final VehicleType type;
    public final int         count;
    public final double      pricePerHour;

    public FleetEntry(VehicleType type, int count, double pricePerHour) {
        if (count <= 0)        throw new IllegalArgumentException("count must be > 0");
        if (pricePerHour <= 0) throw new IllegalArgumentException("price must be > 0");
        this.type         = type;
        this.count        = count;
        this.pricePerHour = pricePerHour;
    }

    /** Convenience factory for readable test code: FleetEntry.of(SUV, 1, 12). */
    public static FleetEntry of(VehicleType type, int count, double pricePerHour) {
        return new FleetEntry(type, count, pricePerHour);
    }
}
