package MC.VehicleRentalServicePerplexity.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Immutable value object representing a [start, end) time window.
 * Bookings are in whole-hour multiples; validation is enforced here.
 */
public final class TimeSlot {

    private final LocalDateTime start;
    private final LocalDateTime end;

    public TimeSlot(LocalDateTime start, LocalDateTime end) {
        Objects.requireNonNull(start, "start must not be null");
        Objects.requireNonNull(end,   "end must not be null");
        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("end must be strictly after start");
        }
        if (start.getMinute() != 0 || start.getSecond() != 0
                || end.getMinute() != 0 || end.getSecond() != 0) {
            throw new IllegalArgumentException("Bookings must be on exact hour boundaries");
        }
        this.start = start;
        this.end   = end;
    }

    public LocalDateTime getStart() { return start; }
    public LocalDateTime getEnd()   { return end; }

    /**
     * Returns true if this slot overlaps with {@code other}.
     * Touching boundaries (end == other.start) do NOT overlap.
     */
    public boolean overlaps(TimeSlot other) {
        return this.start.isBefore(other.end) && other.start.isBefore(this.end);
    }

    /** Checks whether the given point in time falls within this slot. */
    public boolean contains(LocalDateTime point) {
        return !point.isBefore(start) && point.isBefore(end);
    }

    @Override
    public String toString() {
        return start + " → " + end;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TimeSlot)) return false;
        TimeSlot ts = (TimeSlot) o;
        return start.equals(ts.start) && end.equals(ts.end);
    }

    @Override
    public int hashCode() { return Objects.hash(start, end); }
}
