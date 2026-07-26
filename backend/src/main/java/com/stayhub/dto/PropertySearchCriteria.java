package com.stayhub.dto;

import java.math.BigDecimal;

public record PropertySearchCriteria(
        String city,
        String country,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        Integer guests,
        Integer bedrooms
) {
}
