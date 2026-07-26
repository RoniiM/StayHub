package com.stayhub.dto;

import com.stayhub.entity.enums.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record BookingResponse(
        Long bookingId,
        PropertySummaryResponse property,
        GuestSummaryResponse guest,
        LocalDate checkIn,
        LocalDate checkOut,
        BigDecimal totalPrice,
        BookingStatus status,
        LocalDateTime createdAt
) {
}
