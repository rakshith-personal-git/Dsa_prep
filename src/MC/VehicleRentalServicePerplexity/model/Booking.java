package MC.VehicleRentalServicePerplexity.model;

import java.util.UUID;

/**
 * Immutable receipt returned to the caller after a successful rental booking.
 */
public final class Booking {

    private final String    bookingId;
    private final Vehicle   vehicle;
    private final TimeSlot  slot;
    private final double    totalPrice;

    public Booking(Vehicle vehicle, TimeSlot slot) {
        this.bookingId  = UUID.randomUUID().toString().substring(0, 12).toUpperCase();
        this.vehicle    = vehicle;
        this.slot       = slot;
        long hours      = java.time.Duration.between(slot.getStart(), slot.getEnd()).toHours();
        this.totalPrice = hours * vehicle.getPricePerHour();
    }

    public String   getBookingId()  { return bookingId; }
    public Vehicle  getVehicle()    { return vehicle; }
    public TimeSlot getSlot()       { return slot; }
    public double   getTotalPrice() { return totalPrice; }

    @Override
    public String toString() {
        return String.format(
            "Booking[id=%s, vehicle=%s, branch=%s, slot=%s, total=Rs.%.0f]",
            bookingId, vehicle.getType(), vehicle.getBranchName(), slot, totalPrice);
    }
}
