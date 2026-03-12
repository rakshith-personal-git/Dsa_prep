package MC.VehicleRentalServicePerplexity.exception;

/**
 * Thrown when an operation references a branch name that has not been registered.
 */
public class BranchNotFoundException extends RuntimeException {

    public BranchNotFoundException(String message) {
        super(message);
    }

    public BranchNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
