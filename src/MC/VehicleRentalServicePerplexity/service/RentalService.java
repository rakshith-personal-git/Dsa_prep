package MC.VehicleRentalServicePerplexity.service;

import MC.VehicleRentalServicePerplexity.model.*;
import MC.VehicleRentalServicePerplexity.strategy.VehicleSelectionStrategy;

/**
 * Primary service interface for FlipKar.
 *
 * Keeping this as an interface enables easy mocking in unit tests and
 * alternative implementations (e.g., a distributed version backed by Redis).
 */
public interface RentalService {

    /**
     * Registers a new branch with an initial fleet.
     *
     * @param branchName case-insensitive branch name (must be unique)
     * @param entries    one or more {@link FleetEntry} describing type, count, and price
     * @throws IllegalArgumentException if the branch already exists
     */
    void addBranch(String branchName, FleetEntry... entries);

    /**
     * Adds vehicles of an already-known type to an existing branch.
     * The price-per-hour is taken from the first existing vehicle of that type
     * at the branch.
     *
     * @param branchName target branch
     * @param type       vehicle type to add
     * @param count      number of units
     */
    void addVehiclesToBranch(String branchName, VehicleType type, int count);

    /**
     * Attempts to rent a vehicle of the given type for the specified slot.
     * Uses the injected {@link VehicleSelectionStrategy}; falls back to other
     * branches when the preferred branch has no availability.
     *
     * @param type requested vehicle type
     * @param slot requested time window
     * @return a confirmed {@link Booking} if successful
     * @throws MC.VehicleRentalServicePerplexity.exception.NoVehicleAvailableException if none found
     */
    Booking rentVehicle(VehicleType type, TimeSlot slot);

    /**
     * Same as {@link #rentVehicle(VehicleType, TimeSlot)} but with an explicit
     * strategy override for this single call.
     */
    Booking rentVehicle(VehicleType type, TimeSlot slot, VehicleSelectionStrategy strategy);

    /**
     * Prints a human-readable system snapshot for the given time window,
     * showing availability and pricing per branch per vehicle type.
     */
    void printSystemView(TimeSlot querySlot);
}
