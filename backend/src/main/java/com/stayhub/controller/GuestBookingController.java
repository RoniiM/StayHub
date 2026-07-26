package com.stayhub.controller;

import com.stayhub.dto.BookingResponse;
import com.stayhub.dto.CreateBookingRequest;
import com.stayhub.dto.PageResponse;
import com.stayhub.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/guests/{guestId}/bookings")
public class GuestBookingController {

    private final BookingService bookingService;

    public GuestBookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@PathVariable Long guestId,
                                                           @Valid @RequestBody CreateBookingRequest request) {
        BookingResponse response = bookingService.createBooking(guestId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<PageResponse<BookingResponse>> getGuestBookings(
            @PathVariable Long guestId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(bookingService.getGuestBookings(guestId, pageable));
    }

    @PatchMapping("/{bookingId}/cancel")
    public ResponseEntity<BookingResponse> cancelBooking(@PathVariable Long guestId, @PathVariable Long bookingId) {
        return ResponseEntity.ok(bookingService.cancelBooking(guestId, bookingId));
    }
}
