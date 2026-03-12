package MC.VehicleRentalService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory repository for managing {@link Branch} entities.
 *
 * <p>Uses a {@link ConcurrentHashMap} keyed by branch name (lowercase) for
 * thread-safe reads and writes. Branch names are treated as case-insensitive.</p>
 *
 * <p><b>Extension point:</b> To switch to a database-backed store, implement
 * the same public API and inject the new implementation into {@code RentalService}.</p>
 */
public class BranchRepository {

    /**
     * Internal store: branch name (lowercase) → Branch object.
     * ConcurrentHashMap provides thread-safe individual operations.
     */
    private final ConcurrentHashMap<String, Branch> branches = new ConcurrentHashMap<>();

    /**
     * Saves a new branch. If a branch with the same name already exists,
     * this method returns false and does NOT overwrite.
     *
     * @param branch the branch to save
     * @return true if saved successfully, false if a branch with that name already exists
     */
    public boolean save(Branch branch) {
        // putIfAbsent is atomic — no race condition on duplicate check
        return branches.putIfAbsent(branch.getName(), branch) == null;
    }

    /**
     * Finds a branch by its name (case-insensitive).
     *
     * @param name the branch name to look up
     * @return an Optional containing the branch if found, empty otherwise
     */
    public Optional<Branch> findByName(String name) {
        return Optional.ofNullable(branches.get(name.toLowerCase().trim()));
    }

    /**
     * Returns all branches as an unmodifiable list.
     *
     * <p>The order is not guaranteed (HashMap iteration order).</p>
     *
     * @return list of all branches
     */
    public List<Branch> findAll() {
        return Collections.unmodifiableList(new ArrayList<>(branches.values()));
    }

    /**
     * Checks if a branch with the given name exists.
     *
     * @param name the branch name
     * @return true if the branch exists
     */
    public boolean exists(String name) {
        return branches.containsKey(name.toLowerCase().trim());
    }

    /**
     * Clears all branches. Useful for testing.
     */
    public void clear() {
        branches.clear();
    }
}
