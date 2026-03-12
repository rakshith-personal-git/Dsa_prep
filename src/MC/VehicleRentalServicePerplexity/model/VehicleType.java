package MC.VehicleRentalServicePerplexity.model;

/**
 * Canonical vehicle types supported by FlipKar.
 * Adding a new type requires only a new enum constant — no other code changes.
 */
public enum VehicleType {
    SUV, SEDAN, BIKE, HATCHBACK;

    /** Case-insensitive parse so callers can pass raw user input. */
    public static VehicleType fromString(String value) {
        return VehicleType.valueOf(value.trim().toUpperCase());
    }
}
