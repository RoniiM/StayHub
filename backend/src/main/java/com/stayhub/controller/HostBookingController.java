package com.stayhub.controller;

import com.stayhub.dto.BookingResponse;
import com.stayhub.dto.PageResponse;
import com.stayhub.service.BookingService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/hosts/{hostId}/bookings")
public class HostBookingController {

    private final BookingService bookingService;

    public HostBookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public ResponseEntity<PageResponse<BookingResponse>> getHostBookings(
            @PathVariable Long hostId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(bookingService.getHostBookings(hostId, pageable));
    }

    @PatchMapping("/{bookingId}/approve")
    public ResponseEntity<BookingResponse> approveBooking(@PathVariable Long hostId, @PathVariable Long bookingId) {
        return ResponseEntity.ok(bookingService.approveBooking(hostId, bookingId));
    }

    @PatchMapping("/{bookingId}/reject")
    public ResponseEntity<BookingResponse> rejectBooking(@PathVariable Long hostId, @PathVariable Long bookingId) {
        return ResponseEntity.ok(bookingService.rejectBooking(hostId, bookingId));
    }
}
