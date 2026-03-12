package MC.VehicleRentalService;
/**
 * Thrown when a vehicle rental request cannot be fulfilled because no branch
 * has the requested vehicle type available in the requested time slot.
 *
 * <p>This is thrown after the selection strategy has checked ALL branches
 * (including fallback) and found no availability.</p>
 */
public class VehicleNotAvailableException extends RuntimeException {

    /**
     * Creates a new VehicleNotAvailableException.
     *
     * @param vehicleType the type of vehicle that was requested
     */
    public VehicleNotAvailableException(VehicleType vehicleType) {
        super("No " + vehicleType.getDisplayName() + " available across any branch for the requested time slot.");
    }
}
