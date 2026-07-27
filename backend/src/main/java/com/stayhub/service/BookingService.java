package com.stayhub.service;

import com.stayhub.dto.BookingResponse;
import com.stayhub.dto.CreateBookingRequest;
import com.stayhub.dto.PageResponse;
import com.stayhub.entity.User;
import org.springframework.data.domain.Pageable;

public interface BookingService {

    BookingResponse createBooking(Long guestId, CreateBookingRequest request);

    BookingResponse getBookingById(User currentUser, Long bookingId);

    PageResponse<BookingResponse> getGuestBookings(Long guestId, Pageable pageable);

    PageResponse<BookingResponse> getHostBookings(Long hostId, Pageable pageable);

    BookingResponse approveBooking(Long hostId, Long bookingId);

    BookingResponse rejectBooking(Long hostId, Long bookingId);

    BookingResponse cancelBooking(Long guestId, Long bookingId);

    void completeExpiredBookings();
}
