package MC.VehicleRentalServicePerplexity.exception;

/**
 * Thrown when no branch has an available vehicle for the requested type and slot.
 */
public class NoVehicleAvailableException extends RuntimeException {

    public NoVehicleAvailableException(String message) {
        super(message);
    }

    public NoVehicleAvailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
