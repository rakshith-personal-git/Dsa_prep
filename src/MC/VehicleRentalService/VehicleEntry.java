package MC.VehicleRentalService;
/**
 * Represents a vehicle inventory line within a branch.
 *
 * <p>Each VehicleEntry captures:
 * <ul>
 *   <li>The type of vehicle (e.g., SUV, SEDAN)</li>
 *   <li>The total count of that vehicle type at the branch</li>
 *   <li>The fixed rental price per hour</li>
 * </ul>
 *
 * <p>The count can be increased via {@link #addCount(int)} when new vehicles
 * of the same type are onboarded to the branch.</p>
 *
 * <p><b>Note:</b> This class is NOT thread-safe on its own. Thread safety is
 * ensured at the service layer via synchronized blocks.</p>
 */
public class VehicleEntry {

    /** The type of vehicle this entry represents. */
    private final VehicleType vehicleType;

    /** Total number of vehicles of this type at the branch. Mutable — can grow. */
    private int totalCount;

    /** Fixed price per hour for renting this vehicle type. */
    private final int pricePerHour;

    /**
     * Creates a new VehicleEntry.
     *
     * @param vehicleType  the type of vehicle
     * @param totalCount   initial count of vehicles
     * @param pricePerHour rental price per hour in Rs.
     * @throws IllegalArgumentException if count or price is non-positive
     */
    public VehicleEntry(VehicleType vehicleType, int totalCount, int pricePerHour) {
        if (totalCount <= 0) {
            throw new IllegalArgumentException("Vehicle count must be positive. Got: " + totalCount);
        }
        if (pricePerHour <= 0) {
            throw new IllegalArgumentException("Price per hour must be positive. Got: " + pricePerHour);
        }
        this.vehicleType = vehicleType;
        this.totalCount = totalCount;
        this.pricePerHour = pricePerHour;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public int getPricePerHour() {
        return pricePerHour;
    }

    /**
     * Adds more vehicles of this type to the branch inventory.
     *
     * @param count number of vehicles to add (must be positive)
     * @throws IllegalArgumentException if count is non-positive
     */
    public void addCount(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("Count to add must be positive. Got: " + count);
        }
        this.totalCount += count;
    }

    @Override
    public String toString() {
        return totalCount + " " + vehicleType.getDisplayName() + " @ Rs." + pricePerHour + "/hr";
    }
}
