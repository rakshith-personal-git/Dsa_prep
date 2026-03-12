package MC.VehicleRentalService;
/**
 * Thrown when an operation references a branch that does not exist in the system.
 *
 * <p>For example, attempting to add vehicles to a non-existent branch will
 * trigger this exception.</p>
 */
public class BranchNotFoundException extends RuntimeException {

    /**
     * Creates a new BranchNotFoundException.
     *
     * @param branchName the name of the branch that was not found
     */
    public BranchNotFoundException(String branchName) {
        super("Branch not found: '" + branchName + "'");
    }
}
