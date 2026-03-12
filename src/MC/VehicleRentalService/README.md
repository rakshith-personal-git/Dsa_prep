# FlipKar — Vehicle Rental Service

## Problem Summary

Design an in-memory vehicle rental service where multiple branches across a city rent out vehicles (SUV, Sedan, Bike, Hatchback) at fixed per-hour prices. Bookings are in 1-hour multiples. The default selection strategy picks the **cheapest available branch**, with automatic fallback to other branches. A system view shows real-time availability.

## Design Decisions & Patterns

| Pattern | Where | Why |
|---|---|---|
| **Strategy** | `VehicleSelectionStrategy` | Pluggable branch selection (lowest price, nearest, etc.) |
| **Repository** | `BranchRepository`, `BookingRepository` | Encapsulate data access; swap in-memory to DB later |
| **Facade** | `RentalService` | Single entry point for all operations |
| **Value Object** | `TimeSlot`, `VehicleType` | Immutable, equality-by-value |
| **Enum** | `VehicleType` | Type-safe vehicle categorization |

## File Structure

All classes are in the **default package** (flat directory) for simplicity in a machine coding round.

```
VehicalRentalService/
|
|  -- Models --
|-- VehicleType.java                 Enum: SUV, SEDAN, BIKE, HATCHBACK
|-- TimeSlot.java                    Immutable time window with overlap detection
|-- VehicleEntry.java                Vehicle inventory line (type + count + price)
|-- Branch.java                      A rental branch with vehicle entries
|-- Booking.java                     Confirmed rental record
|
|  -- Repositories --
|-- BranchRepository.java            In-memory branch store (ConcurrentHashMap)
|-- BookingRepository.java           In-memory booking store with availability queries
|
|  -- Strategy --
|-- VehicleSelectionStrategy.java    Interface for branch selection
|-- LowestPriceStrategy.java         Default: cheapest available branch
|
|  -- Service --
|-- RentalService.java               Facade: addBranch, addVehicle, rentVehicle, systemView
|
|  -- Exceptions --
|-- BranchNotFoundException.java     Exception: branch not found
|-- DuplicateBranchException.java    Exception: duplicate branch name
|-- VehicleNotAvailableException.java  Exception: no vehicle available
|
|  -- Driver & Tests --
|-- Main.java                        Driver class (demo all test cases)
|-- RentalServiceTest.java           Self-contained test suite (38 tests)
|
|  -- Documentation --
|-- DESIGN.md                        Low-level design document
|-- PROJECT_KT.md                    Knowledge transfer / reading order
|-- README.md                        This file
```

## How to Compile and Run

### Using plain `javac` (no build tool needed)

```bash
# Navigate to the project directory
cd src/MC/VehicalRentalService

# Compile all Java files
javac *.java

# Run the demo driver
java Main

# Run the test suite
java RentalServiceTest

# Clean up class files
rm -f *.class
```

### Using Maven (if you wrap it in a Maven project)

```xml
<!-- pom.xml (place at VehicalRentalService/) -->
<project>
  <modelVersion>4.0.0</modelVersion>
  <groupId>com.flipkar</groupId>
  <artifactId>vehicle-rental-service</artifactId>
  <version>1.0</version>
  <properties>
    <maven.compiler.source>8</maven.compiler.source>
    <maven.compiler.target>8</maven.compiler.target>
  </properties>
</project>
```

Then move source files into `src/main/java/` and run:
```bash
mvn compile
mvn exec:java -Dexec.mainClass="Main"
```

## Expected Sample Output

```
--- Test 5: Rent SUV (20 Feb 10AM-12PM) -> expect malleshwaram ---
[BOOKED] SUV from 'malleshwaram' | 20 Feb 10:00 AM - 20 Feb 12:00 PM | Total: Rs.22

--- Test 6: Rent SUV (20 Feb 10AM-12PM) -> expect koramangala ---
[BOOKED] SUV from 'koramangala' | 20 Feb 10:00 AM - 20 Feb 12:00 PM | Total: Rs.24

--- Test 7: Rent SUV (20 Feb 10AM-12PM) -> expect FAILURE ---
[EXPECTED] No suv available across any branch for the requested time slot.

--- Test 8: System View (20 Feb 11AM-12PM) ---

========== SYSTEM VIEW ==========
Time Slot: 20 Feb 11:00 AM - 20 Feb 12:00 PM
=================================

'Koramangala':
  All "suv" are booked.
  4 "sedan" available for Rs.10
  3 "bike" available for Rs.20

'Jayanagar':
  3 "sedan" available for Rs.11
  3 "bike" available for Rs.30
  4 "hatchback" available for Rs.8

'Malleshwaram':
  All "suv" are booked.
  10 "bike" available for Rs.3
  3 "sedan" available for Rs.10

=================================
```

## How to Extend

| Extension | What to Do |
|---|---|
| **New selection strategy** | Implement `VehicleSelectionStrategy`, inject into `RentalService` |
| **Dynamic pricing** | Add `PricingStrategy` interface; `VehicleEntry` delegates price calc |
| **New vehicle type** | Add enum constant to `VehicleType` |
| **Cancellation** | Add `cancelBooking(id)` to `RentalService`; remove from `BookingRepository` |
| **Persistence** | Swap repository implementations from in-memory to DB-backed |
| **Multi-city** | Add `City` model wrapping multiple branches |
| **Surge pricing** | Implement a `SurgePricingDecorator` around `VehicleEntry.getPricePerHour()` |
