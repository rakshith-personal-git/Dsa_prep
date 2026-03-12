package MC.VehicleRentalService;
import java.util.UUID;

/**
 * Represents a confirmed vehicle rental booking.
 *
 * <p>Each booking captures:
 * <ul>
 *   <li>A unique booking ID (auto-generated UUID)</li>
 *   <li>The branch from which the vehicle was rented</li>
 *   <li>The type of vehicle rented</li>
 *   <li>The time slot for the rental</li>
 *   <li>The total price (pricePerHour × durationInHours)</li>
 * </ul>
 *
 * <p>Bookings are immutable once created — there is no update operation.
 * Cancellation would be handled by removing from the repository (future extension).</p>
 */
public class Booking {

    /** Unique identifier for this booking. */
    private final String bookingId;

    /** Name of the branch from which the vehicle was booked. */
    private final String branchName;

    /** Type of vehicle that was booked. */
    private final VehicleType vehicleType;

    /** The time window for this booking. */
    private final TimeSlot timeSlot;

    /** Total rental price = pricePerHour × duration in hours. */
    private final int totalPrice;

    /**
     * Creates a new Booking with an auto-generated UUID.
     *
     * @param branchName  the branch name
     * @param vehicleType the vehicle type
     * @param timeSlot    the booking time window
     * @param totalPrice  the total rental price
     */
    public Booking(String branchName, VehicleType vehicleType, TimeSlot timeSlot, int totalPrice) {
        this.bookingId = UUID.randomUUID().toString().substring(0, 8); // short ID for readability
        this.branchName = branchName;
        this.vehicleType = vehicleType;
        this.timeSlot = timeSlot;
        this.totalPrice = totalPrice;
    }

    public String getBookingId() {
        return bookingId;
    }

    public String getBranchName() {
        return branchName;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    @Override
    public String toString() {
        return "Booking{id='" + bookingId + "', branch='" + branchName
                + "', type=" + vehicleType + ", slot=" + timeSlot
                + ", price=Rs." + totalPrice + "}";
    }
}
