package MC.VehicleRentalServicePerplexity.strategy;

import MC.VehicleRentalServicePerplexity.model.TimeSlot;
import MC.VehicleRentalServicePerplexity.model.Vehicle;
import MC.VehicleRentalServicePerplexity.model.VehicleType;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Default strategy: pick the vehicle with the lowest hourly price.
 * Ties are broken by vehicle ID (deterministic ordering).
 */
public class LowestPriceStrategy implements VehicleSelectionStrategy {

    @Override
    public Optional<Vehicle> select(List<Vehicle> candidates, VehicleType type, TimeSlot slot) {
        return candidates.stream()
                .min(Comparator.comparingDouble(Vehicle::getPricePerHour)
                               .thenComparing(Vehicle::getVehicleId));
    }
}
