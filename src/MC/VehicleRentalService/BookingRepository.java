package MC.VehicleRentalService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * In-memory repository for managing {@link Booking} entities.
 *
 * <p>Uses a {@link CopyOnWriteArrayList} for thread-safe iteration and writes.
 * This is suitable for a read-heavy workload where bookings are queried frequently
 * (e.g., availability checks) but created less often.</p>
 *
 * <p>The critical method is {@link #countBookings(String, VehicleType, TimeSlot)}
 * which counts how many vehicles of a given type are already booked at a branch
 * during an overlapping time slot. This is the foundation of availability checking.</p>
 */
public class BookingRepository {

    /** Thread-safe list of all bookings. */
    private final CopyOnWriteArrayList<Booking> bookings = new CopyOnWriteArrayList<>();

    /**
     * Saves a new booking.
     *
     * @param booking the booking to persist
     */
    public void save(Booking booking) {
        bookings.add(booking);
    }

    /**
     * Counts the number of vehicles of a specific type that are booked at a
     * specific branch during a time slot that overlaps with the given slot.
     *
     * <p>This is the core availability query. If a branch has 3 sedans and
     * this method returns 2 for a given time slot, then 1 sedan is available.</p>
     *
     * @param branchName  the branch name (case-insensitive)
     * @param vehicleType the vehicle type to check
     * @param timeSlot    the time slot to check for overlaps
     * @return the number of vehicles already booked (overlapping)
     */
    public int countBookings(String branchName, VehicleType vehicleType, TimeSlot timeSlot) {
        String normalizedName = branchName.toLowerCase().trim();
        int count = 0;
        for (Booking booking : bookings) {
            // Match branch, vehicle type, and overlapping time
            if (booking.getBranchName().equals(normalizedName)
                    && booking.getVehicleType() == vehicleType
                    && booking.getTimeSlot().overlaps(timeSlot)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Returns all bookings as an unmodifiable list.
     *
     * @return list of all bookings
     */
    public List<Booking> findAll() {
        return Collections.unmodifiableList(new ArrayList<>(bookings));
    }

    /**
     * Clears all bookings. Useful for testing.
     */
    public void clear() {
        bookings.clear();
    }
}
