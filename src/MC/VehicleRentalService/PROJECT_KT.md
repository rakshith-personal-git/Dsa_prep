# Project Knowledge Transfer — Vehicle Rental Service (FlipKar)

## Recommended Reading Order

Follow this order to build a complete mental model of the project, from foundational types to orchestration to execution.

---

### 1. `DESIGN.md`
**Why first?** Gives you the 30,000-foot view — class diagram, patterns, concurrency model, and extension points. Read this before any code.

---

### 2. `VehicleType.java`
**What:** Enum defining all supported vehicle types (SUV, SEDAN, BIKE, HATCHBACK).
**Why next?** Simplest building block. Every other class references this.

---

### 3. `TimeSlot.java`
**What:** Immutable value object representing a booking window (start to end).
**Why next?** Used by Booking and availability checks. Understand the `overlaps()` method — it's the core of conflict detection.

---

### 4. `VehicleEntry.java`
**What:** Represents a vehicle inventory line in a branch — type, count, price per hour.
**Why next?** Bridges VehicleType with pricing and inventory count.

---

### 5. `Branch.java`
**What:** A rental branch with a name and a list of VehicleEntry items.
**Why next?** Aggregates VehicleEntry. Understand `addVehicles()` and `getEntry()`.

---

### 6. `Booking.java`
**What:** Represents a confirmed rental — which branch, vehicle type, time slot, total price.
**Why next?** The output of a successful `rentVehicle()` call.

---

### 7. `BranchRepository.java`
**What:** In-memory store for branches (ConcurrentHashMap).
**Why next?** Data layer for branches. Simple CRUD.

---

### 8. `BookingRepository.java`
**What:** In-memory store for bookings. Key method: `countBookings()` — counts how many vehicles of a type are booked in an overlapping time slot at a branch.
**Why next?** This is where availability math happens.

---

### 9. `VehicleSelectionStrategy.java`
**What:** Interface with a single method `selectBranch(...)`.
**Why next?** Understand the contract before seeing implementations.

---

### 10. `LowestPriceStrategy.java`
**What:** Default strategy — picks the branch with the lowest per-hour price that has availability.
**Why next?** See how strategy + repository collaborate.

---

### 11. `BranchNotFoundException.java`, `DuplicateBranchException.java`, `VehicleNotAvailableException.java`
**What:** Custom runtime exceptions for clear error signaling.
**Why next?** Quick read. Used by the service layer.

---

### 12. `RentalService.java`
**What:** The **Facade / Orchestrator**. All business operations flow through here: `addBranch`, `addVehicle`, `rentVehicle`, `getSystemView`.
**Why next?** This ties everything together. Read this carefully — it's the heart of the system.

---

### 13. `Main.java`
**What:** Driver class that executes all test cases from the requirements.
**Why next?** See the system in action. Maps 1:1 to the requirement's test cases.

---

### 14. `RentalServiceTest.java`
**What:** Self-contained test class with assertions covering all requirement scenarios plus edge cases (38 tests).
**Why last?** Validates your understanding. If you can predict every assertion outcome, you fully understand the system.

---

## Quick Reference: Key Flows

### Adding a Branch
`Main -> RentalService.addBranch() -> BranchRepository.save()`

### Adding Vehicles to Existing Branch
`Main -> RentalService.addVehicle() -> BranchRepository.findByName() -> Branch.addVehicles()`

### Renting a Vehicle
`Main -> RentalService.rentVehicle() -> VehicleSelectionStrategy.selectBranch() -> BookingRepository.countBookings() (availability check) -> BookingRepository.save()`

### System View
`Main -> RentalService.getSystemView() -> iterates all branches -> BookingRepository.countBookings() per entry -> formats output`
