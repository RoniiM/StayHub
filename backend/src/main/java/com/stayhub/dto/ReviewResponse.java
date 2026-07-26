package com.stayhub.dto;

import java.time.LocalDateTime;

public record ReviewResponse(
        Long reviewId,
        Integer rating,
        String comment,
        LocalDateTime createdAt,
        GuestSummaryResponse guest,
        PropertySummaryResponse property
) {
}
