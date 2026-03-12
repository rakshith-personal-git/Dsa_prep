package MC.VehicleRentalServicePerplexity.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents a physical FlipKar branch.
 *
 * Vehicles are stored in a Map keyed by VehicleType for O(1) type-based lookup.
 * The branch itself is NOT thread-safe at this level; concurrency is handled
 * at the service layer via the vehicle's own internal lock.
 */
public class Branch {

    private final String name;
    /** VehicleType → list of Vehicle instances at this branch. */
    private final Map<VehicleType, List<Vehicle>> inventory = new EnumMap<>(VehicleType.class);

    public Branch(String name) {
        this.name = name.toLowerCase();
    }

    public String getName() { return name; }

    /**
     * Adds one or more vehicle instances of the same type and price to this branch.
     *
     * @param type         vehicle type
     * @param pricePerHour hourly rate in INR
     * @param count        number of units to add
     */
    public void addVehicles(VehicleType type, double pricePerHour, int count) {
        List<Vehicle> list = inventory.computeIfAbsent(type, t -> new ArrayList<>());
        for (int i = 0; i < count; i++) {
            list.add(new Vehicle(type, pricePerHour, name));
        }
    }

    /** Returns all vehicles of the given type, or an empty list if none exist. */
    public List<Vehicle> getVehicles(VehicleType type) {
        return inventory.getOrDefault(type, Collections.emptyList());
    }

    /** Returns all vehicles across every type in this branch. */
    public List<Vehicle> getAllVehicles() {
        return inventory.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    /** Returns the set of VehicleTypes stocked at this branch. */
    public Set<VehicleType> getSupportedTypes() {
        return Collections.unmodifiableSet(inventory.keySet());
    }

    @Override
    public String toString() { return "Branch[" + name + "]"; }
}
