package MC.VehicleRentalServicePerplexity.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * A single physical vehicle unit that can be rented.
 *
 * Thread-safety: individual bookings list is guarded by a ReadWriteLock so that
 * concurrent availability checks (reads) don't block each other while a write
 * (booking) is still atomic.
 */
public class Vehicle {

    private final String      vehicleId;
    private final VehicleType type;
    /** Price per hour in INR. */
    private final double      pricePerHour;
    private final String      branchName;

    /** All confirmed bookings held by this vehicle (no persistence needed). */
    private final List<TimeSlot>   bookings = new ArrayList<>();
    private final ReadWriteLock    lock     = new ReentrantReadWriteLock();

    public Vehicle(VehicleType type, double pricePerHour, String branchName) {
        this.vehicleId    = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.type         = type;
        this.pricePerHour = pricePerHour;
        this.branchName   = branchName;
    }

    public String      getVehicleId()    { return vehicleId; }
    public VehicleType getType()         { return type; }
    public double      getPricePerHour() { return pricePerHour; }
    public String      getBranchName()   { return branchName; }

    /**
     * Returns {@code true} if this vehicle has no conflicting booking for the
     * requested slot.  Read-lock is used so multiple threads may check in parallel.
     */
    public boolean isAvailable(TimeSlot requested) {
        lock.readLock().lock();
        try {
            return bookings.stream().noneMatch(b -> b.overlaps(requested));
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Attempts to add a booking atomically.
     *
     * @return {@code true} if the booking was successfully recorded,
     *         {@code false} if the slot is already taken (lost race).
     */
    public boolean book(TimeSlot slot) {
        lock.writeLock().lock();
        try {
            // Double-checked under write lock to eliminate TOCTOU race
            if (!isAvailableUnsafe(slot)) return false;
            bookings.add(slot);
            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /** Must be called only while holding the write lock. */
    private boolean isAvailableUnsafe(TimeSlot slot) {
        return bookings.stream().noneMatch(b -> b.overlaps(slot));
    }

    /**
     * Returns the list of bookings that overlap with a given query slot.
     * Useful for the system view report.
     */
    public List<TimeSlot> getBookingsOverlapping(TimeSlot query) {
        lock.readLock().lock();
        try {
            return bookings.stream().filter(b -> b.overlaps(query)).toList();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public String toString() {
        return String.format("Vehicle[id=%s, type=%s, branch=%s, price=%.0f/hr]",
                vehicleId, type, branchName, pricePerHour);
    }
}
