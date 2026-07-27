package com.stayhub.controller;

import com.stayhub.dto.BookingResponse;
import com.stayhub.dto.CreateBookingRequest;
import com.stayhub.dto.PageResponse;
import com.stayhub.entity.User;
import com.stayhub.exception.ErrorResponse;
import com.stayhub.security.CurrentUser;
import com.stayhub.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/bookings")
@Tag(name = "Bookings", description = "Booking creation, lifecycle management, and history. All endpoints require authentication.")
@SecurityRequirement(name = "bearerAuth")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @Operation(summary = "Create a booking", description = "Requests a booking for a published property as the authenticated guest.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Booking created with status PENDING"),
            @ApiResponse(responseCode = "400", description = "Validation failed or invalid booking request", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Property not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Dates overlap an existing booking", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@CurrentUser User currentUser,
                                                           @Valid @RequestBody CreateBookingRequest request) {
        BookingResponse response = bookingService.createBooking(currentUser.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get booking details",
            description = "Returns full booking details. Only the guest, the property's host, or an administrator may view a given booking.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Booking found"),
            @ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Caller is not involved in this booking", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Booking not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResponse> getBookingById(@CurrentUser User currentUser,
                                                           @Parameter(description = "ID of the booking") @PathVariable Long bookingId) {
        return ResponseEntity.ok(bookingService.getBookingById(currentUser, bookingId));
    }

    @Operation(summary = "Get my booking history", description = "Returns a paginated list of bookings made by the authenticated guest.")
    @ApiResponse(responseCode = "200", description = "Page of bookings")
    @GetMapping("/me")
    public ResponseEntity<PageResponse<BookingResponse>> getMyBookings(
            @CurrentUser User currentUser,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(bookingService.getGuestBookings(currentUser.getId(), pageable));
    }

    @Operation(summary = "Get bookings for my properties",
            description = "Returns a paginated list of bookings across all properties owned by the authenticated host.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page of bookings"),
            @ApiResponse(responseCode = "403", description = "Caller does not have the HOST role", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasRole('HOST')")
    @GetMapping("/hosted")
    public ResponseEntity<PageResponse<BookingResponse>> getHostedBookings(
            @CurrentUser User currentUser,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(bookingService.getHostBookings(currentUser.getId(), pageable));
    }

    @Operation(summary = "Cancel a booking", description = "Cancels a PENDING or CONFIRMED booking belonging to the authenticated guest.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Booking cancelled"),
            @ApiResponse(responseCode = "403", description = "Caller does not own this booking", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Booking not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Booking cannot be cancelled from its current status", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/{bookingId}/cancel")
    public ResponseEntity<BookingResponse> cancelBooking(@CurrentUser User currentUser,
                                                          @Parameter(description = "ID of the booking") @PathVariable Long bookingId) {
        return ResponseEntity.ok(bookingService.cancelBooking(currentUser.getId(), bookingId));
    }

    @Operation(summary = "Approve a booking", description = "Approves a PENDING booking for a property owned by the authenticated host.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Booking approved"),
            @ApiResponse(responseCode = "403", description = "Caller does not own the property", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Booking not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Booking is not PENDING", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasRole('HOST')")
    @PatchMapping("/{bookingId}/approve")
    public ResponseEntity<BookingResponse> approveBooking(@CurrentUser User currentUser,
                                                           @Parameter(description = "ID of the booking") @PathVariable Long bookingId) {
        return ResponseEntity.ok(bookingService.approveBooking(currentUser.getId(), bookingId));
    }

    @Operation(summary = "Reject a booking", description = "Rejects a PENDING booking for a property owned by the authenticated host.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Booking rejected"),
            @ApiResponse(responseCode = "403", description = "Caller does not own the property", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Booking not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Booking is not PENDING", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasRole('HOST')")
    @PatchMapping("/{bookingId}/reject")
    public ResponseEntity<BookingResponse> rejectBooking(@CurrentUser User currentUser,
                                                          @Parameter(description = "ID of the booking") @PathVariable Long bookingId) {
        return ResponseEntity.ok(bookingService.rejectBooking(currentUser.getId(), bookingId));
    }
}
