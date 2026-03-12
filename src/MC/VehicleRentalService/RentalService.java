package MC.VehicleRentalService;
import java.util.List;
import java.util.Optional;

/**
 * Façade / Orchestrator for the FlipKar Vehicle Rental Service.
 *
 * <p>This is the single entry point for all business operations:
 * <ul>
 *   <li>{@link #addBranch(String, List)} — onboard a new branch with vehicles</li>
 *   <li>{@link #addVehicle(String, VehicleType, int)} — add vehicles to an existing branch</li>
 *   <li>{@link #rentVehicle(VehicleType, TimeSlot)} — rent a vehicle using the configured strategy</li>
 *   <li>{@link #getSystemView(TimeSlot)} — get a formatted view of all branches' availability</li>
 * </ul>
 *
 * <p><b>Concurrency:</b> The {@link #rentVehicle} method is synchronized to prevent
 * double-booking. Two concurrent requests for the last available SUV will be
 * serialized — one will succeed, the other will fail or fall back.</p>
 *
 * <p><b>Strategy pattern:</b> The vehicle/branch selection logic is delegated to
 * a {@link VehicleSelectionStrategy}. The default is lowest-price-first, but this
 * can be swapped at construction time.</p>
 */
public class RentalService {

    /** Repository for branch data. */
    private final BranchRepository branchRepository;

    /** Repository for booking data. */
    private final BookingRepository bookingRepository;

    /** Pluggable strategy for selecting which branch to book from. */
    private final VehicleSelectionStrategy selectionStrategy;

    /**
     * Creates a new RentalService with the given dependencies.
     *
     * @param branchRepository  the branch data store
     * @param bookingRepository the booking data store
     * @param selectionStrategy the vehicle selection strategy
     */
    public RentalService(BranchRepository branchRepository,
                         BookingRepository bookingRepository,
                         VehicleSelectionStrategy selectionStrategy) {
        this.branchRepository = branchRepository;
        this.bookingRepository = bookingRepository;
        this.selectionStrategy = selectionStrategy;
    }

    /**
     * Onboards a new branch with its initial vehicle inventory.
     *
     * <p>Example: addBranch("koramangala", [VehicleEntry(SUV, 1, 12), VehicleEntry(SEDAN, 3, 10)])</p>
     *
     * @param branchName     the name of the new branch
     * @param vehicleEntries the initial vehicle inventory
     * @throws DuplicateBranchException if a branch with this name already exists
     */
    public void addBranch(String branchName, List<VehicleEntry> vehicleEntries) {
        Branch branch = new Branch(branchName, vehicleEntries);
        boolean saved = branchRepository.save(branch);
        if (!saved) {
            throw new DuplicateBranchException(branchName);
        }
        System.out.println("[OK] Branch '" + branch.getName() + "' added with " + vehicleEntries.size() + " vehicle type(s).");
    }

    /**
     * Adds vehicles of an existing type to a specific branch.
     *
     * <p>Example: addVehicle("koramangala", SEDAN, 1) — adds 1 more sedan to Koramangala.</p>
     *
     * @param branchName  the branch to add vehicles to
     * @param vehicleType the type of vehicle to add
     * @param count       number of vehicles to add
     * @throws BranchNotFoundException if the branch does not exist
     */
    public void addVehicle(String branchName, VehicleType vehicleType, int count) {
        Branch branch = branchRepository.findByName(branchName)
                .orElseThrow(() -> new BranchNotFoundException(branchName));

        boolean added = branch.addVehicles(vehicleType, count);
        if (added) {
            System.out.println("[OK] Added " + count + " " + vehicleType.getDisplayName()
                    + "(s) to branch '" + branch.getName() + "'.");
        } else {
            // Vehicle type doesn't exist at this branch — per requirements,
            // we only add to existing types. Log a warning.
            System.out.println("[WARN] Vehicle type '" + vehicleType.getDisplayName()
                    + "' does not exist at branch '" + branch.getName()
                    + "'. Cannot add. Use addBranch to onboard new types.");
        }
    }

    /**
     * Rents a vehicle of the specified type for the given time slot.
     *
     * <p>The selection strategy determines which branch to book from. If the
     * preferred branch has no availability, the strategy automatically falls
     * back to other branches.</p>
     *
     * <p><b>Thread safety:</b> This method is synchronized to prevent race
     * conditions where two threads might both see 1 available vehicle and
     * both try to book it.</p>
     *
     * @param vehicleType the type of vehicle to rent
     * @param timeSlot    the desired booking time slot
     * @return the confirmed Booking
     * @throws VehicleNotAvailableException if no branch has the vehicle available
     */
    public synchronized Booking rentVehicle(VehicleType vehicleType, TimeSlot timeSlot) {
        // Delegate branch selection to the strategy
        List<Branch> allBranches = branchRepository.findAll();

        Optional<Branch> selectedBranch = selectionStrategy.selectBranch(
                allBranches, vehicleType, timeSlot, bookingRepository);

        if (!selectedBranch.isPresent()) {
            throw new VehicleNotAvailableException(vehicleType);
        }

        Branch branch = selectedBranch.get();
        VehicleEntry entry = branch.getEntry(vehicleType)
                .orElseThrow(() -> new IllegalStateException(
                        "Strategy selected a branch without the requested vehicle type. This is a bug."));

        // Calculate total price
        int totalPrice = (int) (entry.getPricePerHour() * timeSlot.getDurationInHours());

        // Create and save the booking
        Booking booking = new Booking(branch.getName(), vehicleType, timeSlot, totalPrice);
        bookingRepository.save(booking);

        System.out.println("[BOOKED] " + vehicleType.getDisplayName().toUpperCase()
                + " from '" + branch.getName() + "' | " + timeSlot
                + " | Total: Rs." + totalPrice
                + " | Booking ID: " + booking.getBookingId());

        return booking;
    }

    /**
     * Generates a system-wide view of vehicle availability for a given time slot.
     *
     * <p>For each branch, shows:
     * <ul>
     *   <li>If all vehicles of a type are booked: "All {type} are booked."</li>
     *   <li>If some are available: "{count} {type} available for Rs.{price}"</li>
     * </ul>
     *
     * @param timeSlot the time slot to check availability for
     * @return a formatted string representing the system view
     */
    public String getSystemView(TimeSlot timeSlot) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n========== SYSTEM VIEW ==========\n");
        sb.append("Time Slot: ").append(timeSlot).append("\n");
        sb.append("=================================\n");

        List<Branch> allBranches = branchRepository.findAll();

        for (Branch branch : allBranches) {
            sb.append("\n'").append(capitalize(branch.getName())).append("':\n");

            for (VehicleEntry entry : branch.getVehicleEntries()) {
                int bookedCount = bookingRepository.countBookings(
                        branch.getName(), entry.getVehicleType(), timeSlot);
                int availableCount = entry.getTotalCount() - bookedCount;

                if (availableCount <= 0) {
                    // All vehicles of this type are booked
                    sb.append("  All \"").append(entry.getVehicleType().getDisplayName())
                            .append("\" are booked.\n");
                } else {
                    // Some vehicles are available
                    sb.append("  ").append(availableCount).append(" \"")
                            .append(entry.getVehicleType().getDisplayName())
                            .append("\" available for Rs.").append(entry.getPricePerHour())
                            .append("\n");
                }
            }
        }

        sb.append("\n=================================\n");
        return sb.toString();
    }

    /**
     * Returns the number of available vehicles of a given type at a given branch
     * for a specific time slot.
     *
     * <p>Useful for programmatic availability checks (e.g., in tests).</p>
     *
     * @param branchName  the branch name
     * @param vehicleType the vehicle type
     * @param timeSlot    the time slot
     * @return number of available vehicles (0 if branch or type not found)
     */
    public int getAvailableCount(String branchName, VehicleType vehicleType, TimeSlot timeSlot) {
        Optional<Branch> branchOpt = branchRepository.findByName(branchName);
        if (!branchOpt.isPresent()) return 0;

        Branch branch = branchOpt.get();
        Optional<VehicleEntry> entryOpt = branch.getEntry(vehicleType);
        if (!entryOpt.isPresent()) return 0;

        int bookedCount = bookingRepository.countBookings(branchName, vehicleType, timeSlot);
        return Math.max(0, entryOpt.get().getTotalCount() - bookedCount);
    }

    /**
     * Capitalizes the first letter of a string.
     *
     * @param s the input string
     * @return the string with the first letter capitalized
     */
    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}
