
package MC.VehicleRentalService;
/**
 * Enum representing the types of vehicles supported by the FlipKar rental service.
 *
 * <p>Each constant maps to a user-facing string (lowercase) so that parsing from
 * command input is straightforward via {@link #fromString(String)}.</p>
 *
 * <p><b>Extension point:</b> To add a new vehicle type (e.g., TRUCK), simply add
 * a new enum constant here. No other code changes are needed unless the new type
 * has special business rules.</p>
 */
public enum VehicleType {

    SUV("suv"),
    SEDAN("sedan"),
    BIKE("bike"),
    HATCHBACK("hatchback");

    /** The lowercase display/parse name for this vehicle type. */
    private final String displayName;

    VehicleType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Parses a case-insensitive string into a {@link VehicleType}.
     *
     * @param text the input string (e.g., "SUV", "sedan", "Bike")
     * @return the matching VehicleType
     * @throws IllegalArgumentException if no match is found
     */
    public static VehicleType fromString(String text) {
        for (VehicleType type : values()) {
            if (type.displayName.equalsIgnoreCase(text.trim())) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown vehicle type: " + text);
    }

    @Override
    public String toString() {
        return displayName;
    }
}
