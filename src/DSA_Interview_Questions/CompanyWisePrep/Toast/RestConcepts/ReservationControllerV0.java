package DSA_Interview_Questions.CompanyWisePrep.Toast.RestConcepts;

/**


import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.ConstraintViolationException;
import java.net.URI;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * =========================================================================================
 * COMPREHENSIVE REST CONTROLLER REVISION SHEET (SPRING BOOT 3+ / SDE3 LEVEL)
 * =========================================================================================
 * 
 * Topics Covered in this File:
 * 1. @RestController, @RequestMapping (Path Versioning), @RequiredArgsConstructor (Constructor Injection)
 * 2. Class-Level @Validated vs Method-Level @Valid
 * 3. ResponseEntity, RFC REST Standards (201 Created + Location Header, 200 OK, 204 No Content)
 * 4. Idempotency Key Handling (@RequestHeader)
 * 5. Pagination Protection (@PageableDefault, maxPageSize) & Custom Generic PagedResponse DTO
 * 6. API Versioning Best Practices (V1 vs V2 Controller isolation)
 * 7. Global Exception Handling (@RestControllerAdvice) mapped to RFC 7807 (ProblemDetail)


// =========================================================================================
// 1. API V1 CONTROLLER
// =========================================================================================

@RestController
@RequestMapping("/api/v1/reservations")
@Validated // Enables Spring's MethodValidationPostProcessor for parameters (@PathVariable, @RequestParam)
@RequiredArgsConstructor // Generates constructor for private final fields (Spring automatically injects without @Autowired)
public class ReservationController {

    private final ReservationService reservationService;

    /**
     * POST /api/v1/reservations
     * - Returns: 201 Created + Location header + Created DTO
     * - @Valid: Validates CreateReservationRequest payload (throws MethodArgumentNotValidException on fail)
     * - X-Idempotency-Key: Header to protect non-idempotent POST calls from duplicate network requests

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(
            @RequestHeader("X-Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody CreateReservationRequest request) {

        ReservationResponse response = reservationService.createReservation(request, idempotencyKey);

        // REST Standard: Build dynamic 'Location' URI header for newly created resource
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getReservationId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    /**
     * GET /api/v1/reservations/{id}
     * - @Validated on class enables @Pattern constraint on @PathVariable
     * - Throws ConstraintViolationException if regex fails

    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponse> getReservationById(
            @PathVariable("id")
            @Pattern(regexp = "^RES-[0-9]{5}$", message = "Reservation ID must match format RES-XXXXX")
            String reservationId) {

        ReservationResponse response = reservationService.getReservationById(reservationId);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/v1/reservations
     * - Unannotated DTO (ReservationSearchFilter) binds query params (?status=CONFIRMED&startDate=2026-10-01)
     * - @PageableDefault: Protects server against Memory Overload/DoS by restricting maxPageSize=100
     * - Returns PagedResponse DTO to prevent exposing Spring Data internal entity abstractions

    @GetMapping
    public ResponseEntity<PagedResponse<ReservationResponse>> searchReservations(
            @Valid ReservationSearchFilter filter,
            @PageableDefault(size = 20, maxPageSize = 100) Pageable pageable) {

        PagedResponse<ReservationResponse> response = reservationService.searchReservations(filter, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * PATCH /api/v1/reservations/{id}
     * - Partial update of existing entity (dates only)

    @PatchMapping("/{id}")
    public ResponseEntity<ReservationResponse> updateReservationDates(
            @PathVariable("id") String reservationId,
            @Valid @RequestBody UpdateReservationDatesRequest request) {

        ReservationResponse response = reservationService.updateDates(reservationId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * DELETE /api/v1/reservations/{id}
     * - Returns: 204 No Content with empty body (ResponseEntity<Void>)

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelReservation(@PathVariable("id") String reservationId) {
        reservationService.cancelReservation(reservationId);
        return ResponseEntity.noContent().build();
    }
}

// =========================================================================================
// 2. API V2 CONTROLLER (SDE3 Best Practice: Separate Controller per Version)
// =========================================================================================

@RestController
@RequestMapping("/api/v2/reservations")
@Validated
@RequiredArgsConstructor
class ReservationV2Controller {

    private final ReservationService reservationService;

    // V2 handles updated search filter contract cleanly without polluting V1 controller
    @GetMapping
    public ResponseEntity<PagedResponse<ReservationResponse>> searchReservationsV2(
            @Valid ReservationSearchFilterV2 filter,
            @PageableDefault(size = 20, maxPageSize = 100) Pageable pageable) {

        PagedResponse<ReservationResponse> response = reservationService.searchReservationsV2(filter, pageable);
        return ResponseEntity.ok(response);
    }
}

// =========================================================================================
// 3. REQUEST & RESPONSE DTOs
// =========================================================================================

@Data
class CreateReservationRequest {

    @NotBlank(message = "Guest name is required")
    @Size(min = 2, max = 50, message = "Guest name must be between 2 and 50 characters")
    private String guestName;

    @NotNull(message = "Check-in date is required")
    @FutureOrPresent(message = "Check-in date must be today or in the future")
    private LocalDate checkInDate;

    @NotNull(message = "Check-out date is required")
    @Future(message = "Check-out date must be in the future")
    private LocalDate checkOutDate;

    @NotEmpty(message = "At least one guest detail is required")
    @Valid // Trigger nested validation on objects within this list
    private List<GuestDetailDto> guests;
}

@Data
class GuestDetailDto {
    @NotBlank(message = "Guest age group is required")
    private String ageGroup; // e.g., ADULT, CHILD

    @Email(message = "Invalid guest contact email format")
    private String contactEmail;
}

@Data
class UpdateReservationDatesRequest {

    @NotNull(message = "Check-in date is required")
    @FutureOrPresent(message = "Check-in date must be today or in the future")
    private LocalDate checkInDate;

    @NotNull(message = "Check-out date is required")
    @Future(message = "Check-out date must be in the future")
    private LocalDate checkOutDate;
}

@Data
class ReservationSearchFilter {
    private String status;
    private LocalDate checkInDate;
}

@Data
class ReservationSearchFilterV2 {
    private String status;
    private LocalDate checkInDate;
    private String propertyId; // Added in V2
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class ReservationResponse {
    private String reservationId;
    private String guestName;
    private String status;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
}

/**
 * Custom Generic Pagination Wrapper DTO.
 * Avoids exposing org.springframework.data.domain.Page directly to API clients.

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
class PagedResponse<T> {

    private List<T> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean last;

    // Static Factory Method to convert Spring Data Page -> PagedResponse DTO
    public static <T> PagedResponse<T> from(Page<T> page) {
        return PagedResponse.<T>builder()
                .content(page.getContent())
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }
}

// =========================================================================================
// 4. DUMMY SERVICE INTERFACE (For Compilation)
// =========================================================================================

interface ReservationService {
    ReservationResponse createReservation(CreateReservationRequest request, String idempotencyKey);
    ReservationResponse getReservationById(String reservationId);
    PagedResponse<ReservationResponse> searchReservations(ReservationSearchFilter filter, Pageable pageable);
    PagedResponse<ReservationResponse> searchReservationsV2(ReservationSearchFilterV2 filter, Pageable pageable);
    ReservationResponse updateDates(String reservationId, UpdateReservationDatesRequest request);
    void cancelReservation(String reservationId);
}

// =========================================================================================
// 5. GLOBAL EXCEPTION HANDLER (RFC 7807 ProblemDetail Format)
// =========================================================================================

@RestControllerAdvice
class GlobalExceptionHandler {

    /**
     * Handles @Valid validation failures on @RequestBody / DTO parameters.
     * Triggered automatically when Bean Validation fails before entering controller method.

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "Invalid request payload constraints"
        );
        problemDetail.setTitle("Validation Failure");
        problemDetail.setType(URI.create("https://api.example.com/errors/validation-error"));
        problemDetail.setProperty("invalidFields", errors);

        return problemDetail;
    }

    /**
     * Handles @Validated validation failures on @PathVariable and @RequestParam.
     * Triggered by Spring's AOP MethodValidationPostProcessor.

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getConstraintViolations().forEach(violation ->
                errors.put(violation.getPropertyPath().toString(), violation.getMessage())
        );

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "Invalid URI path or query parameter constraints"
        );
        problemDetail.setTitle("Parameter Constraint Violation");
        problemDetail.setType(URI.create("https://api.example.com/errors/parameter-error"));
        problemDetail.setProperty("invalidParameters", errors);

        return problemDetail;
    }
}
 *
 */