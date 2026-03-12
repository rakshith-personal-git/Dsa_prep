package MC.VehicleRentalService;
import java.util.List;
import java.util.Optional;

/**
 * Default vehicle selection strategy: picks the branch with the <b>lowest
 * per-hour price</b> for the requested vehicle type that has at least one
 * vehicle available in the requested time slot.
 *
 * <p><b>Algorithm:</b>
 * <ol>
 *   <li>Iterate over all branches.</li>
 *   <li>For each branch, check if it has the requested vehicle type.</li>
 *   <li>Check availability: totalCount − bookedCount > 0.</li>
 *   <li>Among available branches, pick the one with the lowest pricePerHour.</li>
 *   <li>If there's a tie, the first one encountered wins (iteration order).</li>
 * </ol>
 *
 * <p>This strategy satisfies the requirement: "lowest price as the default
 * choice of selection of vehicle".</p>
 */
public class LowestPriceStrategy implements VehicleSelectionStrategy {

    /**
     * Selects the branch with the lowest price per hour for the given vehicle type
     * that has availability in the given time slot.
     *
     * @param branches          all branches in the system
     * @param vehicleType       the requested vehicle type
     * @param timeSlot          the desired time slot
     * @param bookingRepository used to check how many vehicles are already booked
     * @return the cheapest available branch, or empty if none available
     */
    @Override
    public Optional<Branch> selectBranch(List<Branch> branches,
                                          VehicleType vehicleType,
                                          TimeSlot timeSlot,
                                          BookingRepository bookingRepository) {
        Branch bestBranch = null;
        int lowestPrice = Integer.MAX_VALUE;

        for (Branch branch : branches) {
            // Check if this branch even has the requested vehicle type
            Optional<VehicleEntry> entryOpt = branch.getEntry(vehicleType);
            if (!entryOpt.isPresent()) {
                continue; // Branch doesn't offer this vehicle type
            }

            VehicleEntry entry = entryOpt.get();

            // Calculate availability: total vehicles minus currently booked ones
            int bookedCount = bookingRepository.countBookings(
                    branch.getName(), vehicleType, timeSlot);
            int availableCount = entry.getTotalCount() - bookedCount;

            if (availableCount <= 0) {
                continue; // No vehicles available at this branch for this slot
            }

            // Pick the branch with the lowest price
            if (entry.getPricePerHour() < lowestPrice) {
                lowestPrice = entry.getPricePerHour();
                bestBranch = branch;
            }
        }

        return Optional.ofNullable(bestBranch);
    }
}
