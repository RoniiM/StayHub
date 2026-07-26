package com.stayhub.service.impl;

import com.stayhub.dto.BookingResponse;
import com.stayhub.dto.CreateBookingRequest;
import com.stayhub.dto.PageResponse;
import com.stayhub.entity.Booking;
import com.stayhub.entity.Property;
import com.stayhub.entity.User;
import com.stayhub.entity.enums.BookingStatus;
import com.stayhub.entity.enums.PropertyStatus;
import com.stayhub.exception.BookingOverlapException;
import com.stayhub.exception.BookingOwnershipException;
import com.stayhub.exception.InvalidBookingRequestException;
import com.stayhub.exception.InvalidBookingStatusTransitionException;
import com.stayhub.exception.PropertyOwnershipException;
import com.stayhub.exception.ResourceNotFoundException;
import com.stayhub.repository.BookingRepository;
import com.stayhub.repository.PropertyRepository;
import com.stayhub.repository.UserRepository;
import com.stayhub.service.BookingMapper;
import com.stayhub.service.BookingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Transactional
public class BookingServiceImpl implements BookingService {

    private static final List<BookingStatus> BLOCKING_STATUSES = List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED);

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;

    public BookingServiceImpl(BookingRepository bookingRepository,
                               UserRepository userRepository,
                               PropertyRepository propertyRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.propertyRepository = propertyRepository;
    }

    @Override
    public BookingResponse createBooking(Long guestId, CreateBookingRequest request) {
        User guest = getUserOrThrow(guestId);
        Property property = propertyRepository.findById(request.propertyId())
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + request.propertyId()));

        if (property.getStatus() != PropertyStatus.PUBLISHED) {
            throw new InvalidBookingRequestException("Property is not available for booking");
        }

        if (property.getHost().getId().equals(guestId)) {
            throw new InvalidBookingRequestException("Hosts cannot book their own property");
        }

        validateDates(request.checkIn(), request.checkOut());

        List<Booking> overlapping = bookingRepository.findOverlappingBookings(
                property.getId(), request.checkIn(), request.checkOut(), BLOCKING_STATUSES);
        if (!overlapping.isEmpty()) {
            throw new BookingOverlapException("Property is not available for the selected dates");
        }

        long nights = ChronoUnit.DAYS.between(request.checkIn(), request.checkOut());
        BigDecimal totalPrice = property.getPricePerNight().multiply(BigDecimal.valueOf(nights));

        Booking booking = Booking.builder()
                .checkIn(request.checkIn())
                .checkOut(request.checkOut())
                .totalPrice(totalPrice)
                .status(BookingStatus.PENDING)
                .guest(guest)
                .property(property)
                .build();

        Booking saved = bookingRepository.save(booking);
        return BookingMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponse getBookingById(Long bookingId) {
        return BookingMapper.toResponse(getBookingOrThrow(bookingId));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BookingResponse> getGuestBookings(Long guestId, Pageable pageable) {
        getUserOrThrow(guestId);
        Page<Booking> page = bookingRepository.findByGuestId(guestId, pageable);
        return PageResponse.from(page.map(BookingMapper::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BookingResponse> getHostBookings(Long hostId, Pageable pageable) {
        getUserOrThrow(hostId);
        Page<Booking> page = bookingRepository.findByPropertyHostId(hostId, pageable);
        return PageResponse.from(page.map(BookingMapper::toResponse));
    }

    @Override
    public BookingResponse approveBooking(Long hostId, Long bookingId) {
        Booking booking = getBookingOrThrow(bookingId);
        validateHostOwnsBooking(hostId, booking);

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new InvalidBookingStatusTransitionException(
                    "Booking must be PENDING to be approved, current status: " + booking.getStatus());
        }

        booking.setStatus(BookingStatus.CONFIRMED);
        return BookingMapper.toResponse(booking);
    }

    @Override
    public BookingResponse rejectBooking(Long hostId, Long bookingId) {
        Booking booking = getBookingOrThrow(bookingId);
        validateHostOwnsBooking(hostId, booking);

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new InvalidBookingStatusTransitionException(
                    "Booking must be PENDING to be rejected, current status: " + booking.getStatus());
        }

        booking.setStatus(BookingStatus.REJECTED);
        return BookingMapper.toResponse(booking);
    }

    @Override
    public BookingResponse cancelBooking(Long guestId, Long bookingId) {
        Booking booking = getBookingOrThrow(bookingId);

        if (!booking.getGuest().getId().equals(guestId)) {
            throw new BookingOwnershipException(
                    "Guest with id: " + guestId + " does not own booking with id: " + bookingId);
        }

        if (booking.getStatus() != BookingStatus.PENDING && booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new InvalidBookingStatusTransitionException(
                    "Booking cannot be cancelled from its current status: " + booking.getStatus());
        }

        booking.setStatus(BookingStatus.CANCELLED);
        return BookingMapper.toResponse(booking);
    }

    @Override
    public void completeExpiredBookings() {
        List<Booking> expired = bookingRepository.findByStatusAndCheckOutBefore(
                BookingStatus.CONFIRMED, LocalDate.now());
        expired.forEach(booking -> booking.setStatus(BookingStatus.COMPLETED));
    }

    private void validateDates(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn.isBefore(LocalDate.now())) {
            throw new InvalidBookingRequestException("Check-in date cannot be in the past");
        }
        if (!checkIn.isBefore(checkOut)) {
            throw new InvalidBookingRequestException("Check-in date must be before check-out date");
        }
    }

    private void validateHostOwnsBooking(Long hostId, Booking booking) {
        if (!booking.getProperty().getHost().getId().equals(hostId)) {
            throw new PropertyOwnershipException(
                    "Host with id: " + hostId + " does not own the property associated with booking: " + booking.getId());
        }
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    private Booking getBookingOrThrow(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));
    }
}
