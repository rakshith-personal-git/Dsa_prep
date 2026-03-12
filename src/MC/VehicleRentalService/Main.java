package MC.VehicleRentalService;
import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * Driver class for the FlipKar Vehicle Rental Service.
 *
 * <p>This class demonstrates all the features required by the problem statement
 * by executing the exact test cases from the requirements document.</p>
 *
 * <p><b>How to run:</b></p>
 * <pre>
 *   cd src/MC/VehicalRentalService
 *   javac *.java
 *   java Main
 * </pre>
 *
 * <p><b>Test cases executed:</b></p>
 * <ol>
 *   <li>Add branch 'koramangala' with SUV, SEDAN, BIKE</li>
 *   <li>Add branch 'jayanagar' with SEDAN, BIKE, HATCHBACK</li>
 *   <li>Add branch 'malleshwaram' with SUV, BIKE, SEDAN</li>
 *   <li>Add 1 sedan to koramangala</li>
 *   <li>Rent SUV (20th Feb 10:00 AM – 12:00 PM) → should book from malleshwaram (Rs.11 &lt; Rs.12)</li>
 *   <li>Rent SUV (20th Feb 10:00 AM – 12:00 PM) → should book from koramangala (only one left)</li>
 *   <li>Rent SUV (20th Feb 10:00 AM – 12:00 PM) → should FAIL (no SUV available)</li>
 *   <li>Print system view for 20th Feb 11:00 AM – 12:00 PM</li>
 * </ol>
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║     FlipKar Vehicle Rental Service — Demo       ║");
        System.out.println("╚══════════════════════════════════════════════════╝\n");

        // ─── Bootstrap: create repositories and service with lowest-price strategy ───
        BranchRepository branchRepo = new BranchRepository();
        BookingRepository bookingRepo = new BookingRepository();
        LowestPriceStrategy strategy = new LowestPriceStrategy();
        RentalService rentalService = new RentalService(branchRepo, bookingRepo, strategy);

        // ─── Test Case 1: Add branch 'koramangala' ───
        System.out.println("--- Test 1: Add branch 'koramangala' ---");
        rentalService.addBranch("koramangala", Arrays.asList(
                new VehicleEntry(VehicleType.SUV, 1, 12),      // 1 SUV @ Rs.12/hr
                new VehicleEntry(VehicleType.SEDAN, 3, 10),     // 3 Sedan @ Rs.10/hr
                new VehicleEntry(VehicleType.BIKE, 3, 20)       // 3 Bike @ Rs.20/hr
        ));

        // ─── Test Case 2: Add branch 'jayanagar' ───
        System.out.println("\n--- Test 2: Add branch 'jayanagar' ---");
        rentalService.addBranch("jayanagar", Arrays.asList(
                new VehicleEntry(VehicleType.SEDAN, 3, 11),     // 3 Sedan @ Rs.11/hr
                new VehicleEntry(VehicleType.BIKE, 3, 30),      // 3 Bike @ Rs.30/hr
                new VehicleEntry(VehicleType.HATCHBACK, 4, 8)   // 4 Hatchback @ Rs.8/hr
        ));

        // ─── Test Case 3: Add branch 'malleshwaram' ───
        System.out.println("\n--- Test 3: Add branch 'malleshwaram' ---");
        rentalService.addBranch("malleshwaram", Arrays.asList(
                new VehicleEntry(VehicleType.SUV, 1, 11),       // 1 SUV @ Rs.11/hr
                new VehicleEntry(VehicleType.BIKE, 10, 3),      // 10 Bike @ Rs.3/hr
                new VehicleEntry(VehicleType.SEDAN, 3, 10)      // 3 Sedan @ Rs.10/hr
        ));

        // ─── Test Case 4: Add 1 sedan to koramangala ───
        System.out.println("\n--- Test 4: Add 1 sedan to 'koramangala' ---");
        rentalService.addVehicle("koramangala", VehicleType.SEDAN, 1);

        // Define the common time slot: 20th Feb 10:00 AM – 12:00 PM
        // Using year 2025 for a concrete date
        LocalDateTime feb20_10am = LocalDateTime.of(2025, 2, 20, 10, 0);
        LocalDateTime feb20_12pm = LocalDateTime.of(2025, 2, 20, 12, 0);
        TimeSlot slot10to12 = new TimeSlot(feb20_10am, feb20_12pm);

        // ─── Test Case 5: Rent SUV → should book from malleshwaram (Rs.11 < Rs.12) ───
        System.out.println("\n--- Test 5: Rent SUV (20 Feb 10AM-12PM) → expect malleshwaram ---");
        Booking booking1 = rentalService.rentVehicle(VehicleType.SUV, slot10to12);

        // ─── Test Case 6: Rent SUV → should book from koramangala (malleshwaram is full) ───
        System.out.println("\n--- Test 6: Rent SUV (20 Feb 10AM-12PM) → expect koramangala ---");
        Booking booking2 = rentalService.rentVehicle(VehicleType.SUV, slot10to12);

        // ─── Test Case 7: Rent SUV → should FAIL (no SUV available anywhere) ───
        System.out.println("\n--- Test 7: Rent SUV (20 Feb 10AM-12PM) → expect FAILURE ---");
        try {
            rentalService.rentVehicle(VehicleType.SUV, slot10to12);
            System.out.println("[ERROR] Should have thrown VehicleNotAvailableException!");
        } catch (VehicleNotAvailableException e) {
            System.out.println("[EXPECTED] " + e.getMessage());
        }

        // ─── Test Case 8: Print system view ───
        // Using 11:00 AM – 12:00 PM as per the requirement (overlaps with the 10-12 bookings)
        System.out.println("\n--- Test 8: System View (20 Feb 11AM-12PM) ---");
        LocalDateTime feb20_11am = LocalDateTime.of(2025, 2, 20, 11, 0);
        TimeSlot viewSlot = new TimeSlot(feb20_11am, feb20_12pm);
        String systemView = rentalService.getSystemView(viewSlot);
        System.out.println(systemView);

        // ─── Additional Demo: Rent a vehicle in a non-overlapping slot ───
        System.out.println("--- Bonus: Rent SUV in a different time slot (should succeed) ---");
        LocalDateTime feb20_1pm = LocalDateTime.of(2025, 2, 20, 13, 0);
        LocalDateTime feb20_3pm = LocalDateTime.of(2025, 2, 20, 15, 0);
        TimeSlot slot1to3 = new TimeSlot(feb20_1pm, feb20_3pm);
        Booking booking3 = rentalService.rentVehicle(VehicleType.SUV, slot1to3);

        System.out.println("\n--- Bonus: System View for 1PM-3PM slot ---");
        System.out.println(rentalService.getSystemView(slot1to3));

        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║              Demo Complete!                      ║");
        System.out.println("╚══════════════════════════════════════════════════╝");
    }
}
