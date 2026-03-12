package MC.VehicleRentalService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Represents a rental branch in the FlipKar service.
 *
 * <p>A branch has a unique name (case-insensitive, stored lowercase) and maintains
 * a list of {@link VehicleEntry} items representing its vehicle inventory.</p>
 *
 * <p><b>Key operations:</b>
 * <ul>
 *   <li>{@link #addVehicles(VehicleType, int)} — onboard new vehicles of an existing type</li>
 *   <li>{@link #getEntry(VehicleType)} — look up inventory for a specific vehicle type</li>
 *   <li>{@link #getVehicleEntries()} — read-only view of all inventory</li>
 * </ul>
 *
 * <p><b>Thread safety:</b> Not thread-safe on its own. Callers (service layer)
 * must synchronize when modifying entries.</p>
 */
public class Branch {

    /** Branch name, stored in lowercase for case-insensitive matching. */
    private final String name;

    /** Vehicle inventory at this branch. Each entry is a (type, count, price) tuple. */
    private final List<VehicleEntry> vehicleEntries;

    /**
     * Creates a new Branch with the given name and initial vehicle entries.
     *
     * @param name           the branch name (will be lowercased)
     * @param vehicleEntries initial vehicle inventory; must not be null
     */
    public Branch(String name, List<VehicleEntry> vehicleEntries) {
        this.name = name.toLowerCase().trim();
        // Defensive copy — we own this list
        this.vehicleEntries = new ArrayList<>(vehicleEntries);
    }

    public String getName() {
        return name;
    }

    /**
     * Returns an unmodifiable view of the vehicle entries at this branch.
     *
     * @return read-only list of vehicle entries
     */
    public List<VehicleEntry> getVehicleEntries() {
        return Collections.unmodifiableList(vehicleEntries);
    }

    /**
     * Looks up the vehicle entry for a given type.
     *
     * @param type the vehicle type to search for
     * @return an Optional containing the entry if found, empty otherwise
     */
    public Optional<VehicleEntry> getEntry(VehicleType type) {
        for (VehicleEntry entry : vehicleEntries) {
            if (entry.getVehicleType() == type) {
                return Optional.of(entry);
            }
        }
        return Optional.empty();
    }

    /**
     * Adds more vehicles of an existing type to this branch.
     *
     * <p>If the vehicle type already exists, its count is incremented.
     * If the vehicle type does not exist at this branch, a new entry cannot be
     * created here because we don't know the price — use
     * {@link #addNewVehicleEntry(VehicleEntry)} instead.</p>
     *
     * @param type  the vehicle type to add
     * @param count number of vehicles to add
     * @return true if the type existed and count was incremented, false otherwise
     */
    public boolean addVehicles(VehicleType type, int count) {
        Optional<VehicleEntry> existing = getEntry(type);
        if (existing.isPresent()) {
            existing.get().addCount(count);
            return true;
        }
        return false;
    }

    /**
     * Adds a completely new vehicle entry (new type) to this branch.
     *
     * @param entry the new vehicle entry to add
     */
    public void addNewVehicleEntry(VehicleEntry entry) {
        vehicleEntries.add(entry);
    }

    @Override
    public String toString() {
        return "Branch{name='" + name + "', vehicles=" + vehicleEntries + "}";
    }
}
