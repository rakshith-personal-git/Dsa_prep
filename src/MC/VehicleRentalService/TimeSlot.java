package MC.VehicleRentalService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Immutable value object representing a booking time window.
 *
 * <p>A TimeSlot is defined by a start time (inclusive) and an end time (exclusive).
 * The key method {@link #overlaps(TimeSlot)} determines whether two time slots
 * conflict — this is the foundation of the availability-checking logic.</p>
 *
 * <p><b>Immutability guarantee:</b> All fields are final and set only via the
 * constructor. This makes TimeSlot inherently thread-safe.</p>
 */
public class TimeSlot {

    /** Start of the booking window (inclusive). */
    private final LocalDateTime startTime;

    /** End of the booking window (exclusive). */
    private final LocalDateTime endTime;

    /** Formatter used for display purposes. */
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("dd MMM hh:mm a");

    /**
     * Creates a new TimeSlot.
     *
     * @param startTime the start of the window (inclusive)
     * @param endTime   the end of the window (exclusive); must be after startTime
     * @throws IllegalArgumentException if endTime is not after startTime
     */
    public TimeSlot(LocalDateTime startTime, LocalDateTime endTime) {
        if (!endTime.isAfter(startTime)) {
            throw new IllegalArgumentException(
                    "End time must be after start time. Got start=" + startTime + ", end=" + endTime);
        }
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    /**
     * Calculates the duration of this time slot in whole hours.
     *
     * @return number of hours between start and end
     */
    public long getDurationInHours() {
        return java.time.Duration.between(startTime, endTime).toHours();
    }

    /**
     * Checks whether this time slot overlaps with another.
     *
     * <p>Two slots overlap if one starts before the other ends AND ends after
     * the other starts. This is the standard interval-overlap check.</p>
     *
     * <p>Example: [10:00–12:00) overlaps with [11:00–13:00) → true.
     * [10:00–12:00) does NOT overlap with [12:00–14:00) → false (boundary touch).</p>
     *
     * @param other the other time slot to check against
     * @return true if the two slots overlap
     */
    public boolean overlaps(TimeSlot other) {
        return this.startTime.isBefore(other.endTime) && this.endTime.isAfter(other.startTime);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeSlot timeSlot = (TimeSlot) o;
        return startTime.equals(timeSlot.startTime) && endTime.equals(timeSlot.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startTime, endTime);
    }

    @Override
    public String toString() {
        return startTime.format(DISPLAY_FORMAT) + " - " + endTime.format(DISPLAY_FORMAT);
    }
}
