package MC.VehicleRentalServicePerplexity;

import MC.VehicleRentalServicePerplexity.exception.NoVehicleAvailableException;
import MC.VehicleRentalServicePerplexity.model.*;
import MC.VehicleRentalServicePerplexity.service.*;
import MC.VehicleRentalServicePerplexity.strategy.LowestPriceStrategy;

import java.time.LocalDateTime;

/**
 * Driver class that exercises every feature of FlipKar end-to-end.
 *
 * Run via Maven:  mvn package -q && java -jar target/flipkar-1.0.0-jar-with-dependencies.jar
 * Run via javac:  see README.md
 */
public class Main {

    public static void main(String[] args) {

        RentalService service = new RentalServiceImpl(new LowestPriceStrategy());

        // ── 1. Onboard branches ──────────────────────────────────────────────
        System.out.println("\n===== STEP 1: Onboard Branches =====");
        service.addBranch("koramangala",
                FleetEntry.of(VehicleType.SUV,      1, 12),
                FleetEntry.of(VehicleType.SEDAN,     3, 10),
                FleetEntry.of(VehicleType.BIKE,      3, 20));

        service.addBranch("jayanagar",
                FleetEntry.of(VehicleType.SEDAN,     3, 11),
                FleetEntry.of(VehicleType.BIKE,      3, 30),
                FleetEntry.of(VehicleType.HATCHBACK, 4,  8));

        service.addBranch("malleshwaram",
                FleetEntry.of(VehicleType.SUV,       1, 11),
                FleetEntry.of(VehicleType.BIKE,     10,  3),
                FleetEntry.of(VehicleType.SEDAN,     3, 10));

        // ── 2. Add a vehicle to an existing branch ───────────────────────────
        System.out.println("\n===== STEP 2: Add Vehicle to Existing Branch =====");
        service.addVehiclesToBranch("koramangala", VehicleType.SEDAN, 1);

        // ── 3. Rent vehicles ─────────────────────────────────────────────────
        System.out.println("\n===== STEP 3: Rent Vehicles =====");

        LocalDateTime feb20_10 = LocalDateTime.of(2025, 2, 20, 10, 0);
        LocalDateTime feb20_12 = LocalDateTime.of(2025, 2, 20, 12, 0);
        TimeSlot slot10to12 = new TimeSlot(feb20_10, feb20_12);

        // Should book from malleshwaram (Rs.11 < Rs.12)
        Booking b1 = service.rentVehicle(VehicleType.SUV, slot10to12);
        System.out.println("Expected: malleshwaram  Got: " + b1.getVehicle().getBranchName());

        // Should book from koramangala (only remaining SUV)
        Booking b2 = service.rentVehicle(VehicleType.SUV, slot10to12);
        System.out.println("Expected: koramangala  Got: " + b2.getVehicle().getBranchName());

        // Should FAIL — no SUV anywhere
        System.out.println("\n--- Attempting third SUV rental (should fail) ---");
        try {
            service.rentVehicle(VehicleType.SUV, slot10to12);
            System.out.println("ERROR: Should have thrown NoVehicleAvailableException");
        } catch (NoVehicleAvailableException e) {
            System.out.println("[EXPECTED FAILURE] " + e.getMessage());
        }

        // ── 4. System view ───────────────────────────────────────────────────
        System.out.println("\n===== STEP 4: System View =====");
        LocalDateTime feb20_11 = LocalDateTime.of(2025, 2, 20, 11, 0);
        service.printSystemView(new TimeSlot(feb20_11, feb20_12));

        // ── 5. Edge cases ────────────────────────────────────────────────────
        System.out.println("===== STEP 5: Edge Cases =====");

        // Back-to-back slots: same vehicle should be re-available
        LocalDateTime feb20_12end = LocalDateTime.of(2025, 2, 20, 14, 0);
        TimeSlot slot12to14 = new TimeSlot(feb20_12, feb20_12end);
        System.out.println("\n-- Booking SUV for immediately following slot --");
        Booking b3 = service.rentVehicle(VehicleType.SUV, slot12to14);
        System.out.println("Back-to-back slot booked: " + b3);

        // Hatchback only in jayanagar
        TimeSlot hatchbackSlot = new TimeSlot(
                LocalDateTime.of(2025, 2, 21, 9, 0),
                LocalDateTime.of(2025, 2, 21, 11, 0));
        System.out.println("\n-- Hatchback rental (only in jayanagar) --");
        Booking b4 = service.rentVehicle(VehicleType.HATCHBACK, hatchbackSlot);
        System.out.println("Hatchback from: " + b4.getVehicle().getBranchName());

        System.out.println("\n===== ALL DEMO STEPS COMPLETE =====\n");
    }
}
