package MC.VehicleRentalService;
/**
 * Thrown when attempting to add a branch with a name that already exists.
 *
 * <p>Branch names are unique (case-insensitive) in the system.</p>
 */
public class DuplicateBranchException extends RuntimeException {

    /**
     * Creates a new DuplicateBranchException.
     *
     * @param branchName the duplicate branch name
     */
    public DuplicateBranchException(String branchName) {
        super("Branch already exists: '" + branchName + "'");
    }
}
