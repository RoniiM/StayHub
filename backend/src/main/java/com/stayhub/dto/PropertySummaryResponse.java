package com.stayhub.dto;

import java.math.BigDecimal;

public record PropertySummaryResponse(
        Long id,
        String title,
        String city,
        String country,
        BigDecimal pricePerNight
) {
}
