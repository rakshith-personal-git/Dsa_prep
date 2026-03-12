package MC.VehicleRentalServicePerplexity.exception;

/**
 * Thrown when trying to onboard a branch whose name is already registered.
 */
public class BranchAlreadyExistsException extends RuntimeException {

    public BranchAlreadyExistsException(String message) {
        super(message);
    }

    public BranchAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
