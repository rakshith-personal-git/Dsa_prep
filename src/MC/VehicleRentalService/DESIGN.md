# Low-Level Design — Vehicle Rental Service (FlipKar)

## Problem Summary

Design an in-memory vehicle rental service where:
- Multiple branches exist across a city, each with limited vehicles of different types.
- Vehicles have fixed per-hour pricing.
- Bookings are made in 1-hour slot multiples.
- Default vehicle selection is **lowest price first**, extensible via Strategy pattern.
- If a branch has no availability, the system falls back to other branches using the same strategy.
- A system view shows booked/available vehicles across all branches for a given time slot.

## Class Diagram (text representation)

```
                        ┌──────────────────────┐
                        │   RentalService       │  ◄── Façade / Orchestrator
                        │  (singleton-like)     │
                        └──────┬───────────────┘
                               │ uses
               ┌───────────────┼───────────────────┐
               ▼               ▼                   ▼
      ┌────────────┐  ┌──────────────┐   ┌──────────────────────┐
      │ BranchRepo │  │ BookingRepo  │   │ VehicleSelectionStrat │ (interface)
      │ (Registry) │  │ (Registry)   │   │ egy                   │
      └─────┬──────┘  └──────┬───────┘   └──────────┬───────────┘
            │                │                       │
            ▼                ▼                       ├── LowestPriceStrategy
      ┌──────────┐   ┌────────────┐                 └── (future strategies)
      │  Branch   │   │  Booking   │
      │           │   │            │
      └─────┬─────��   └────────────┘
            │ has many
            ▼
      ┌──────────────┐
      │ VehicleEntry  │  (type + count + pricePerHour)
      └──────────────┘

      ┌──────────────┐
      │ VehicleType   │  (Enum: SUV, SEDAN, BIKE, HATCHBACK)
      └──────────────┘

      ┌──────────────┐
      │ TimeSlot      │  (startTime, endTime — value object)
      └──────────────┘
```

## Key Interfaces

| Interface / Abstract | Methods | Purpose |
|---|---|---|
| `VehicleSelectionStrategy` | `selectBranch(List<Branch>, VehicleType, TimeSlot, BookingRepository)` | Pluggable algorithm for choosing which branch to book from |
| `RentalService` | `addBranch`, `addVehicle`, `rentVehicle`, `getSystemView` | Façade — single entry point for all operations |

## Design Patterns Used

| Pattern | Where | Why |
|---|---|---|
| **Strategy** | `VehicleSelectionStrategy` | Decouple vehicle/branch selection logic; easily swap lowest-price for highest-rated, nearest-branch, etc. |
| **Repository** | `BranchRepository`, `BookingRepository` | Encapsulate data access; easy to swap in-memory with DB later |
| **Façade** | `RentalService` | Single orchestrator hides internal complexity from the driver |
| **Value Object** | `TimeSlot`, `VehicleType` | Immutable, equality-by-value semantics |
| **Builder (light)** | `Booking` | Clean construction of booking records |
| **Enum** | `VehicleType` | Type-safe vehicle categorization |

## Concurrency Model

| Guarantee | Mechanism |
|---|---|
| Thread-safe branch registry | `ConcurrentHashMap` in `BranchRepository` |
| Thread-safe booking registry | `CopyOnWriteArrayList` in `BookingRepository` |
| Atomic availability check + book | `synchronized` block in `RentalService.rentVehicle()` to prevent double-booking |
| Immutable value objects | `TimeSlot` is immutable — safe to share across threads |

## File Structure

```
VehicalRentalService/
├── VehicleType.java                — Enum: SUV, SEDAN, BIKE, HATCHBACK
├── TimeSlot.java                   — Immutable time window with overlap detection
├── VehicleEntry.java               �� Vehicle inventory line (type + count + price)
├── Branch.java                     — A rental branch with vehicle entries
├── Booking.java                    — Confirmed rental record
├── BranchRepository.java           — In-memory branch store (ConcurrentHashMap)
├── BookingRepository.java          — In-memory booking store with availability queries
├── VehicleSelectionStrategy.java   — Interface for branch selection
├── LowestPriceStrategy.java        — Default: cheapest available branch
├── RentalService.java              — Façade: addBranch, addVehicle, rentVehicle, systemView
├── BranchNotFoundException.java    — Exception: branch not found
├── DuplicateBranchException.java   — Exception: duplicate branch name
├── VehicleNotAvailableException.java — Exception: no vehicle available
├── Main.java                       — Driver class demonstrating all test cases
├── RentalServiceTest.java          — Self-contained test class (38 tests)
├── DESIGN.md                       — This file
├── PROJECT_KT.md                   — Knowledge transfer / reading order
└── README.md                       — Compile/run instructions, expected output
```

All classes are in the default package for simplicity in a machine coding round.
Logical grouping is by responsibility (models, repositories, strategy, service, exceptions).

## How to Extend

1. **New vehicle selection strategy**: Implement `VehicleSelectionStrategy` and inject into `RentalService`.
2. **Dynamic pricing**: Add a `PricingStrategy` interface; `VehicleEntry` delegates price calculation.
3. **New vehicle types**: Add enum constant to `VehicleType`.
4. **Cancellation**: Add `cancelBooking(bookingId)` to `RentalService`; remove from `BookingRepository`.
5. **Persistence**: Swap repository implementations from in-memory to DB-backed.
6. **Multi-city**: Add a `City` model wrapping multiple branches.
