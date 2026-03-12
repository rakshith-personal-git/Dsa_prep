package MC.VehicleRentalService;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

/**
 * Self-contained test class for the FlipKar Vehicle Rental Service.
 *
 * <p>This class does NOT depend on JUnit or any external testing framework.
 * It uses a simple assertion helper method and prints PASS/FAIL for each test.</p>
 *
 * <p><b>Test coverage:</b></p>
 * <ul>
 *   <li>All test cases from the requirements document</li>
 *   <li>Edge cases: duplicate branch, non-existent branch, boundary time slots,
 *       non-overlapping bookings, adding vehicles to existing branch, etc.</li>
 * </ul>
 *
 * <p><b>How to run:</b></p>
 * <pre>
 *   cd src/MC/VehicalRentalService
 *   javac *.java
 *   java RentalServiceTest
 * </pre>
 */
public class RentalServiceTest {

    /** Counters for test results. */
    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║     FlipKar Rental Service — Test Suite         ║");
        System.out.println("╚══════════════════════════════════════════════════╝\n");

        // Run all test groups
        testAddBranch();
        testAddVehicle();
        testRentVehicle_RequirementScenario();
        testRentVehicle_NonOverlappingSlots();
        testRentVehicle_PartialOverlap();
        testRentVehicle_AllBranchesFull();
        testSystemView();
        testTimeSlotValidation();
        testVehicleTypeFromString();
        testDuplicateBranch();
        testAddVehicleToNonExistentBranch();
        testBoundaryTimeSlots();
        testAvailableCountAPI();
        testMultipleVehicleTypesBooking();

        // Summary
        System.out.println("\n╔══════════════════════════════════════════════════╗");
        System.out.printf("║  Results: %d PASSED, %d FAILED, %d TOTAL         ║%n",
                passed, failed, passed + failed);
        System.out.println("╚══════════════════════════════════════════════════╝");

        if (failed > 0) {
            System.exit(1); // Non-zero exit code for CI pipelines
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // Test: Add Branch
    // ═══════════════════════════════════════════════════════════════════

    private static void testAddBranch() {
        System.out.println("── testAddBranch ──");
        RentalService service = createService();

        // Should succeed
        service.addBranch("koramangala", Arrays.asList(
                new VehicleEntry(VehicleType.SUV, 1, 12),
                new VehicleEntry(VehicleType.SEDAN, 3, 10)
        ));
        assertEqual("Branch added", 1, service.getAvailableCount("koramangala", VehicleType.SUV, defaultSlot()));
        assertEqual("Sedan count", 3, service.getAvailableCount("koramangala", VehicleType.SEDAN, defaultSlot()));
    }

    // ═══════════════════════════════════════════════════════════════════
    // Test: Add Vehicle to Existing Branch
    // ═══════════════════════════════════════════════════════════════════

    private static void testAddVehicle() {
        System.out.println("\n── testAddVehicle ──");
        RentalService service = createService();

        service.addBranch("koramangala", Arrays.asList(
                new VehicleEntry(VehicleType.SEDAN, 3, 10)
        ));

        // Add 1 more sedan
        service.addVehicle("koramangala", VehicleType.SEDAN, 1);
        assertEqual("Sedan count after add", 4,
                service.getAvailableCount("koramangala", VehicleType.SEDAN, defaultSlot()));
    }

    // ═══════════════════════════════════════════════════════════════════
    // Test: Requirement Scenario (exact test cases from problem statement)
    // ═══════════════════════════════════════════════════════════════════

    private static void testRentVehicle_RequirementScenario() {
        System.out.println("\n── testRentVehicle_RequirementScenario ──");
        RentalService service = createService();

        // Setup: add all 3 branches as per requirements
        service.addBranch("koramangala", Arrays.asList(
                new VehicleEntry(VehicleType.SUV, 1, 12),
                new VehicleEntry(VehicleType.SEDAN, 3, 10),
                new VehicleEntry(VehicleType.BIKE, 3, 20)
        ));
        service.addBranch("jayanagar", Arrays.asList(
                new VehicleEntry(VehicleType.SEDAN, 3, 11),
                new VehicleEntry(VehicleType.BIKE, 3, 30),
                new VehicleEntry(VehicleType.HATCHBACK, 4, 8)
        ));
        service.addBranch("malleshwaram", Arrays.asList(
                new VehicleEntry(VehicleType.SUV, 1, 11),
                new VehicleEntry(VehicleType.BIKE, 10, 3),
                new VehicleEntry(VehicleType.SEDAN, 3, 10)
        ));

        // Add 1 sedan to koramangala
        service.addVehicle("koramangala", VehicleType.SEDAN, 1);

        TimeSlot slot = slot(10, 12);

        // Rent SUV #1 → should book from malleshwaram (Rs.11 < Rs.12)
        Booking b1 = service.rentVehicle(VehicleType.SUV, slot);
        assertEqual("First SUV from malleshwaram", "malleshwaram", b1.getBranchName());
        assertEqual("First SUV price", 22, b1.getTotalPrice()); // 11 * 2 hours

        // Rent SUV #2 → should book from koramangala (malleshwaram is full)
        Booking b2 = service.rentVehicle(VehicleType.SUV, slot);
        assertEqual("Second SUV from koramangala", "koramangala", b2.getBranchName());
        assertEqual("Second SUV price", 24, b2.getTotalPrice()); // 12 * 2 hours

        // Rent SUV #3 → should FAIL
        boolean exceptionThrown = false;
        try {
            service.rentVehicle(VehicleType.SUV, slot);
        } catch (VehicleNotAvailableException e) {
            exceptionThrown = true;
        }
        assertTrue("Third SUV should fail", exceptionThrown);
    }

    // ═══════════════════════════════════════════════════════════════════
    // Test: Non-overlapping time slots should not conflict
    // ═══════════════════════════════════════════════════════════════════

    private static void testRentVehicle_NonOverlappingSlots() {
        System.out.println("\n── testRentVehicle_NonOverlappingSlots ──");
        RentalService service = createService();

        service.addBranch("branch1", Collections.singletonList(
                new VehicleEntry(VehicleType.SUV, 1, 10)
        ));

        TimeSlot morning = slot(10, 12);
        TimeSlot afternoon = slot(12, 14); // Starts exactly when morning ends — no overlap

        // Book morning slot
        Booking b1 = service.rentVehicle(VehicleType.SUV, morning);
        assertEqual("Morning booking", "branch1", b1.getBranchName());

        // Book afternoon slot — should succeed because no overlap
        Booking b2 = service.rentVehicle(VehicleType.SUV, afternoon);
        assertEqual("Afternoon booking (no overlap)", "branch1", b2.getBranchName());
    }

    // ═══════════════════════════════════════════════════════════════════
    // Test: Partial overlap should block booking
    // ══════════════════════════���════════════════════════════════════════

    private static void testRentVehicle_PartialOverlap() {
        System.out.println("\n── testRentVehicle_PartialOverlap ──");
        RentalService service = createService();

        service.addBranch("branch1", Collections.singletonList(
                new VehicleEntry(VehicleType.SUV, 1, 10)
        ));

        TimeSlot slot1 = slot(10, 12);
        TimeSlot slot2 = slot(11, 13); // Overlaps with slot1 (11:00-12:00)

        service.rentVehicle(VehicleType.SUV, slot1);

        // Should fail because the single SUV is booked during overlapping period
        boolean exceptionThrown = false;
        try {
            service.rentVehicle(VehicleType.SUV, slot2);
        } catch (VehicleNotAvailableException e) {
            exceptionThrown = true;
        }
        assertTrue("Partial overlap should block booking", exceptionThrown);
    }

    // ═══════════════════════════════════════════════════════════════════
    // Test: All branches full for a vehicle type
    // ═══════════════════════════════════════════════════════════════════

    private static void testRentVehicle_AllBranchesFull() {
        System.out.println("\n── testRentVehicle_AllBranchesFull ──");
        RentalService service = createService();

        // Only 1 bike across all branches
        service.addBranch("b1", Collections.singletonList(new VehicleEntry(VehicleType.BIKE, 1, 5)));
        service.addBranch("b2", Collections.singletonList(new VehicleEntry(VehicleType.BIKE, 1, 10)));

        TimeSlot slot = slot(10, 12);

        // Book both bikes
        service.rentVehicle(VehicleType.BIKE, slot);
        service.rentVehicle(VehicleType.BIKE, slot);

        // Third should fail
        boolean exceptionThrown = false;
        try {
            service.rentVehicle(VehicleType.BIKE, slot);
        } catch (VehicleNotAvailableException e) {
            exceptionThrown = true;
        }
        assertTrue("All branches full should throw exception", exceptionThrown);
    }

    // ═══════════════════════════════════════════════════════════════════
    // Test: System View output
    // ═══════════════════════════════════════════════════════════════════

    private static void testSystemView() {
        System.out.println("\n── testSystemView ──");
        RentalService service = createService();

        service.addBranch("testbranch", Arrays.asList(
                new VehicleEntry(VehicleType.SUV, 2, 15),
                new VehicleEntry(VehicleType.SEDAN, 1, 10)
        ));

        TimeSlot slot = slot(10, 12);

        // Book 1 SUV
        service.rentVehicle(VehicleType.SUV, slot);

        String view = service.getSystemView(slot);

        // Should show 1 SUV available and 1 sedan available
        assertTrue("View contains available SUV", view.contains("1 \"suv\" available"));
        assertTrue("View contains available sedan", view.contains("1 \"sedan\" available"));

        // Book the second SUV
        service.rentVehicle(VehicleType.SUV, slot);
        view = service.getSystemView(slot);

        // Now all SUVs should be booked
        assertTrue("View shows all SUV booked", view.contains("All \"suv\" are booked"));
    }

    // ═══════════════════════════════════════════════════════════════════
    // Test: TimeSlot validation
    // ═══════════════════════════════════════════════════════════════════

    private static void testTimeSlotValidation() {
        System.out.println("\n── testTimeSlotValidation ──");

        // End time before start time should throw
        boolean exceptionThrown = false;
        try {
            new TimeSlot(
                    LocalDateTime.of(2025, 2, 20, 12, 0),
                    LocalDateTime.of(2025, 2, 20, 10, 0)
            );
        } catch (IllegalArgumentException e) {
            exceptionThrown = true;
        }
        assertTrue("Invalid time slot should throw", exceptionThrown);

        // Same start and end should throw
        exceptionThrown = false;
        try {
            LocalDateTime same = LocalDateTime.of(2025, 2, 20, 10, 0);
            new TimeSlot(same, same);
        } catch (IllegalArgumentException e) {
            exceptionThrown = true;
        }
        assertTrue("Zero-duration time slot should throw", exceptionThrown);
    }

    // ═══════════════════════════════════════════════════════════════════
    // Test: VehicleType.fromString
    // ═══════════════════════════════════════════════════════════════════

    private static void testVehicleTypeFromString() {
        System.out.println("\n── testVehicleTypeFromString ──");

        assertEqual("Parse SUV", VehicleType.SUV, VehicleType.fromString("suv"));
        assertEqual("Parse SEDAN", VehicleType.SEDAN, VehicleType.fromString("Sedan"));
        assertEqual("Parse BIKE", VehicleType.BIKE, VehicleType.fromString("BIKE"));
        assertEqual("Parse HATCHBACK", VehicleType.HATCHBACK, VehicleType.fromString("hatchback"));

        boolean exceptionThrown = false;
        try {
            VehicleType.fromString("truck");
        } catch (IllegalArgumentException e) {
            exceptionThrown = true;
        }
        assertTrue("Unknown type should throw", exceptionThrown);
    }

    // ═══════════════════════════════════════════════════════════════════
    // Test: Duplicate branch
    // ═══════════════════════════════════════════════════════════════════

    private static void testDuplicateBranch() {
        System.out.println("\n── testDuplicateBranch ──");
        RentalService service = createService();

        service.addBranch("koramangala", Collections.singletonList(
                new VehicleEntry(VehicleType.SUV, 1, 10)
        ));

        boolean exceptionThrown = false;
        try {
            service.addBranch("koramangala", Collections.singletonList(
                    new VehicleEntry(VehicleType.BIKE, 2, 5)
            ));
        } catch (DuplicateBranchException e) {
            exceptionThrown = true;
        }
        assertTrue("Duplicate branch should throw", exceptionThrown);
    }

    // ═══════════════════════════════════════════════════════════════════
    // Test: Add vehicle to non-existent branch
    // ═════════════════════════════════════���═════════════════════════════

    private static void testAddVehicleToNonExistentBranch() {
        System.out.println("\n── testAddVehicleToNonExistentBranch ──");
        RentalService service = createService();

        boolean exceptionThrown = false;
        try {
            service.addVehicle("nonexistent", VehicleType.SUV, 1);
        } catch (BranchNotFoundException e) {
            exceptionThrown = true;
        }
        assertTrue("Non-existent branch should throw", exceptionThrown);
    }

    // ═══════════════════════════════════════════════════════════════════
    // Test: Boundary time slots (adjacent, not overlapping)
    // ═══════════════════════════════════════════════════════════════════

    private static void testBoundaryTimeSlots() {
        System.out.println("\n── testBoundaryTimeSlots ──");

        TimeSlot slot1 = slot(10, 12);
        TimeSlot slot2 = slot(12, 14);
        TimeSlot slot3 = slot(11, 13);

        // Adjacent slots should NOT overlap
        assertTrue("Adjacent slots don't overlap", !slot1.overlaps(slot2));
        assertTrue("Adjacent slots don't overlap (reverse)", !slot2.overlaps(slot1));

        // Partially overlapping slots SHOULD overlap
        assertTrue("Partial overlap detected", slot1.overlaps(slot3));
        assertTrue("Partial overlap detected (reverse)", slot3.overlaps(slot1));

        // Same slot should overlap with itself
        assertTrue("Same slot overlaps itself", slot1.overlaps(slot1));
    }

    // ═══════════════════════════════════════════════════════════════════
    // Test: getAvailableCount API
    // ═══════════════════════════════════════════════════════════════════

    private static void testAvailableCountAPI() {
        System.out.println("\n── testAvailableCountAPI ──");
        RentalService service = createService();

        service.addBranch("b1", Collections.singletonList(
                new VehicleEntry(VehicleType.SUV, 3, 10)
        ));

        TimeSlot slot = slot(10, 12);

        assertEqual("Initial count", 3, service.getAvailableCount("b1", VehicleType.SUV, slot));

        service.rentVehicle(VehicleType.SUV, slot);
        assertEqual("After 1 booking", 2, service.getAvailableCount("b1", VehicleType.SUV, slot));

        service.rentVehicle(VehicleType.SUV, slot);
        assertEqual("After 2 bookings", 1, service.getAvailableCount("b1", VehicleType.SUV, slot));

        service.rentVehicle(VehicleType.SUV, slot);
        assertEqual("After 3 bookings", 0, service.getAvailableCount("b1", VehicleType.SUV, slot));

        // Non-existent branch should return 0
        assertEqual("Non-existent branch", 0,
                service.getAvailableCount("nonexistent", VehicleType.SUV, slot));

        // Non-existent vehicle type at branch should return 0
        assertEqual("Non-existent type", 0,
                service.getAvailableCount("b1", VehicleType.BIKE, slot));
    }

    // ═══════════════════════════════════════════════════════════════════
    // Test: Multiple vehicle types booking at same branch
    // ═══════════════════════════════════════════════════════════════════

    private static void testMultipleVehicleTypesBooking() {
        System.out.println("\n── testMultipleVehicleTypesBooking ──");
        RentalService service = createService();

        service.addBranch("b1", Arrays.asList(
                new VehicleEntry(VehicleType.SUV, 1, 10),
                new VehicleEntry(VehicleType.SEDAN, 1, 8)
        ));

        TimeSlot slot = slot(10, 12);

        // Book the SUV
        service.rentVehicle(VehicleType.SUV, slot);

        // Sedan should still be available
        assertEqual("Sedan still available after SUV booked", 1,
                service.getAvailableCount("b1", VehicleType.SEDAN, slot));

        // Book the sedan
        Booking b = service.rentVehicle(VehicleType.SEDAN, slot);
        assertEqual("Sedan booked from b1", "b1", b.getBranchName());
        assertEqual("Sedan price", 16, b.getTotalPrice()); // 8 * 2 hours
    }

    // ═══════════════════════════════════════════════════════════════════
    // Helper Methods
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Creates a fresh RentalService instance with empty repositories.
     * Each test gets its own isolated service to avoid cross-test contamination.
     */
    private static RentalService createService() {
        return new RentalService(
                new BranchRepository(),
                new BookingRepository(),
                new LowestPriceStrategy()
        );
    }

    /**
     * Creates a TimeSlot on 20th Feb 2025 with the given start and end hours.
     *
     * @param startHour start hour (0-23)
     * @param endHour   end hour (0-23)
     * @return a TimeSlot for that range
     */
    private static TimeSlot slot(int startHour, int endHour) {
        return new TimeSlot(
                LocalDateTime.of(2025, 2, 20, startHour, 0),
                LocalDateTime.of(2025, 2, 20, endHour, 0)
        );
    }

    /**
     * Default time slot for simple tests: 20 Feb 10:00 AM – 12:00 PM.
     */
    private static TimeSlot defaultSlot() {
        return slot(10, 12);
    }

    /**
     * Asserts that two values are equal. Prints PASS or FAIL.
     */
    private static void assertEqual(String testName, Object expected, Object actual) {
        if (expected.equals(actual)) {
            System.out.println("  ✅ PASS: " + testName);
            passed++;
        } else {
            System.out.println("  ❌ FAIL: " + testName + " — expected: " + expected + ", got: " + actual);
            failed++;
        }
    }

    /**
     * Asserts that a condition is true. Prints PASS or FAIL.
     */
    private static void assertTrue(String testName, boolean condition) {
        if (condition) {
            System.out.println("  ✅ PASS: " + testName);
            passed++;
        } else {
            System.out.println("  ❌ FAIL: " + testName);
            failed++;
        }
    }
}
