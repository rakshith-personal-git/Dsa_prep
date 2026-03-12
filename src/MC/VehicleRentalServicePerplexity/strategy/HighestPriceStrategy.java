package MC.VehicleRentalServicePerplexity.strategy;

import MC.VehicleRentalServicePerplexity.model.TimeSlot;
import MC.VehicleRentalServicePerplexity.model.Vehicle;
import MC.VehicleRentalServicePerplexity.model.VehicleType;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Alternative strategy: prefer the most expensive (premium) vehicle.
 * Provided as an example of how easily the Strategy pattern extends.
 */
public class HighestPriceStrategy implements VehicleSelectionStrategy {

    @Override
    public Optional<Vehicle> select(List<Vehicle> candidates, VehicleType type, TimeSlot slot) {
        return candidates.stream()
                .max(Comparator.comparingDouble(Vehicle::getPricePerHour)
                               .thenComparing(Vehicle::getVehicleId));
    }
}
