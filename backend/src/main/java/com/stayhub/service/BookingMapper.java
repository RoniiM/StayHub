package com.stayhub.service;

import com.stayhub.dto.BookingResponse;
import com.stayhub.dto.GuestSummaryResponse;
import com.stayhub.dto.PropertySummaryResponse;
import com.stayhub.entity.Booking;
import com.stayhub.entity.Property;
import com.stayhub.entity.User;

public final class BookingMapper {

    private BookingMapper() {
    }

    public static BookingResponse toResponse(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                toPropertySummary(booking.getProperty()),
                toGuestSummary(booking.getGuest()),
                booking.getCheckIn(),
                booking.getCheckOut(),
                booking.getTotalPrice(),
                booking.getStatus(),
                booking.getCreatedAt()
        );
    }

    public static PropertySummaryResponse toPropertySummary(Property property) {
        return new PropertySummaryResponse(
                property.getId(),
                property.getTitle(),
                property.getCity(),
                property.getCountry(),
                property.getPricePerNight()
        );
    }

    public static GuestSummaryResponse toGuestSummary(User guest) {
        return new GuestSummaryResponse(
                guest.getId(),
                guest.getFirstName(),
                guest.getLastName(),
                guest.getProfileImageUrl()
        );
    }
}
