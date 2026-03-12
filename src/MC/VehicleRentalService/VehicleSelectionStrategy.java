package MC.VehicleRentalService;
import java.util.List;
import java.util.Optional;

/**
 * Strategy interface for selecting which branch to book a vehicle from.
 *
 * <p>The default implementation is {@link LowestPriceStrategy}, which picks the
 * branch offering the lowest per-hour price for the requested vehicle type,
 * provided that branch has availability.</p>
 *
 * <p><b>Extension point:</b> Implement this interface to create alternative
 * selection strategies, such as:
 * <ul>
 *   <li>Nearest branch (requires location data)</li>
 *   <li>Highest-rated branch</li>
 *   <li>Round-robin for load balancing</li>
 *   <li>Highest price first (premium selection)</li>
 * </ul>
 *
 * <p>Inject the desired strategy into {@code RentalService} at construction time.</p>
 */
public interface VehicleSelectionStrategy {

    /**
     * Selects the best branch to book a vehicle from, based on the strategy's criteria.
     *
     * @param branches          all available branches in the system
     * @param vehicleType       the type of vehicle requested
     * @param timeSlot          the desired booking time slot
     * @param bookingRepository the booking repository (needed to check current availability)
     * @return an Optional containing the selected branch, or empty if no branch has availability
     */
    Optional<Branch> selectBranch(List<Branch> branches,
                                   VehicleType vehicleType,
                                   TimeSlot timeSlot,
                                   BookingRepository bookingRepository);
}
