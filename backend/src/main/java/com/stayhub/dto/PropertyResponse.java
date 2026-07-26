package com.stayhub.dto;

import com.stayhub.entity.enums.PropertyStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public record PropertyResponse(
        Long id,
        String title,
        String description,
        BigDecimal pricePerNight,
        Integer maxGuests,
        Integer bedrooms,
        Integer bathrooms,
        String city,
        String country,
        String streetAddress,
        String postalCode,
        Double latitude,
        Double longitude,
        PropertyStatus status,
        HostSummaryResponse host,
        List<PropertyImageResponse> images,
        Set<AmenityResponse> amenities,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
