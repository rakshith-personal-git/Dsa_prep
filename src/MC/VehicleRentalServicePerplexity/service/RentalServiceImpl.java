package MC.VehicleRentalServicePerplexity.service;

import MC.VehicleRentalServicePerplexity.exception.BranchAlreadyExistsException;
import MC.VehicleRentalServicePerplexity.exception.BranchNotFoundException;
import MC.VehicleRentalServicePerplexity.exception.NoVehicleAvailableException;
import MC.VehicleRentalServicePerplexity.model.*;
import MC.VehicleRentalServicePerplexity.strategy.VehicleSelectionStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Core implementation of {@link RentalService}.
 *
 * Design decisions:
 * <ul>
 *   <li>Branches are stored in a {@link ConcurrentHashMap} so branch
 *       registration is thread-safe without coarse locking.</li>
 *   <li>Vehicle booking atomicity is delegated to {@link Vehicle#book(TimeSlot)}
 *       which holds a per-vehicle write-lock, keeping contention minimal.</li>
 *   <li>The fallback-across-branches logic lives here, not in the strategy,
 *       so strategies remain pure "pick best from a list" objects.</li>
 * </ul>
 */
public class RentalServiceImpl implements RentalService {

    /** All registered branches keyed by lower-cased name. */
    private final Map<String, Branch>       branches  = new ConcurrentHashMap<>();
    /** Insertion-ordered list of branch names — used for deterministic fallback. */
    private final List<String>              branchOrder = Collections.synchronizedList(new ArrayList<>());
    private final VehicleSelectionStrategy  defaultStrategy;

    public RentalServiceImpl(VehicleSelectionStrategy defaultStrategy) {
        this.defaultStrategy = defaultStrategy;
    }

    // ── Branch management ────────────────────────────────────────────────────

    @Override
    public void addBranch(String branchName, FleetEntry... entries) {
        String key = branchName.toLowerCase();
        if (branches.containsKey(key)) {
            throw new BranchAlreadyExistsException("Branch already exists: " + branchName);
        }
        Branch branch = new Branch(key);
        for (FleetEntry entry : entries) {
            branch.addVehicles(entry.type, entry.pricePerHour, entry.count);
        }
        branches.put(key, branch);
        branchOrder.add(key);
        System.out.printf("[BRANCH ADDED] %s%n", branchName);
    }

    @Override
    public void addVehiclesToBranch(String branchName, VehicleType type, int count) {
        Branch branch = getBranch(branchName);
        // Derive price from existing vehicles of the same type
        List<Vehicle> existing = branch.getVehicles(type);
        if (existing.isEmpty()) {
            throw new IllegalStateException(
                "No existing vehicles of type " + type + " at " + branchName +
                ". Use addBranch to introduce a new type.");
        }
        double price = existing.get(0).getPricePerHour();
        branch.addVehicles(type, price, count);
        System.out.printf("[VEHICLE ADDED] %d %s added to %s%n", count, type, branchName);
    }

    // ── Rental logic ─────────────────────────────────────────────────────────

    @Override
    public Booking rentVehicle(VehicleType type, TimeSlot slot) {
        return rentVehicle(type, slot, defaultStrategy);
    }

    @Override
    public Booking rentVehicle(VehicleType type, TimeSlot slot, VehicleSelectionStrategy strategy) {
        // Iterate branches in registration order for deterministic fallback
        for (String branchKey : branchOrder) {
            Branch branch = branches.get(branchKey);
            Optional<Booking> booking = attemptBookingAtBranch(branch, type, slot, strategy);
            if (booking.isPresent()) {
                Booking b = booking.get();
                System.out.printf("[BOOKED] %s%n", b);
                return b;
            }
        }
        throw new NoVehicleAvailableException(
            "No " + type + " available in any branch for slot: " + slot);
    }

    /**
     * Tries to find and book a suitable vehicle at a single branch.
     * Returns empty if no vehicle is available or the booking races fail.
     */
    private Optional<Booking> attemptBookingAtBranch(
            Branch branch, VehicleType type, TimeSlot slot,
            VehicleSelectionStrategy strategy) {

        // Collect all free vehicles of the requested type
        List<Vehicle> available = branch.getVehicles(type).stream()
                .filter(v -> v.isAvailable(slot))
                .collect(Collectors.toList());

        if (available.isEmpty()) return Optional.empty();

        // Strategy picks the best candidate
        Optional<Vehicle> chosen = strategy.select(available, type, slot);
        if (chosen.isEmpty()) return Optional.empty();

        // Attempt to commit the booking (handles concurrent races via vehicle lock)
        Vehicle vehicle = chosen.get();
        if (vehicle.book(slot)) {
            return Optional.of(new Booking(vehicle, slot));
        }
        // Race condition: someone else grabbed it — retry with remaining candidates
        available.remove(vehicle);
        while (!available.isEmpty()) {
            chosen = strategy.select(available, type, slot);
            if (chosen.isEmpty()) break;
            vehicle = chosen.get();
            if (vehicle.book(slot)) return Optional.of(new Booking(vehicle, slot));
            available.remove(vehicle);
        }
        return Optional.empty();
    }

    // ── System view ──────────────────────────────────────────────────────────

    @Override
    public void printSystemView(TimeSlot querySlot) {
        System.out.println("\n═══════════════════════════════════════════════════════");
        System.out.printf( "  SYSTEM VIEW  |  Slot: %s%n", querySlot);
        System.out.println("═══════════════════════════════════════════════════════");

        for (String branchKey : branchOrder) {
            Branch branch = branches.get(branchKey);
            System.out.printf("%n  ▸ Branch: %s%n", branch.getName().toUpperCase());
            System.out.println("  " + "─".repeat(50));

            for (VehicleType type : VehicleType.values()) {
                List<Vehicle> fleet = branch.getVehicles(type);
                if (fleet.isEmpty()) continue;

                long available = fleet.stream().filter(v -> v.isAvailable(querySlot)).count();
                long booked    = fleet.size() - available;
                double price   = fleet.get(0).getPricePerHour();

                if (available == 0) {
                    System.out.printf("    %-12s  All %d booked.%n", type, booked);
                } else {
                    System.out.printf("    %-12s  %d available  (Rs.%.0f/hr)  |  %d booked%n",
                            type, available, price, booked);
                }
            }
        }
        System.out.println("═══════════════════════════════════════════════════════\n");
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private Branch getBranch(String name) {
        Branch b = branches.get(name.toLowerCase());
        if (b == null) throw new BranchNotFoundException("Branch not found: " + name);
        return b;
    }

    /** Exposed for testing — returns an unmodifiable view of all branches. */
    public Map<String, Branch> getBranches() {
        return Collections.unmodifiableMap(branches);
    }
}
