package MC.VehicleRentalServicePerplexity.strategy;

import MC.VehicleRentalServicePerplexity.model.TimeSlot;
import MC.VehicleRentalServicePerplexity.model.Vehicle;
import MC.VehicleRentalServicePerplexity.model.VehicleType;

import java.util.List;
import java.util.Optional;

/**
 * Strategy interface for choosing among available vehicles.
 *
 * New strategies (e.g., highest-rated, nearest branch) can be added by
 * implementing this interface without touching any service code.
 */
public interface VehicleSelectionStrategy {

    /**
     * Selects the best vehicle from {@code candidates} for the given slot.
     *
     * @param candidates  vehicles of the desired type that are free in the slot
     * @param type        the requested vehicle type (contextual hint for strategy)
     * @param slot        the requested time window
     * @return            the chosen vehicle, or empty if candidates is empty
     */
    Optional<Vehicle> select(List<Vehicle> candidates, VehicleType type, TimeSlot slot);
}
