package com.stayhub.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record UpdatePropertyRequest(

        @NotBlank
        @Size(max = 150)
        String title,

        @NotBlank
        String description,

        @NotNull
        @Positive
        BigDecimal pricePerNight,

        @NotNull
        @Min(1)
        Integer maxGuests,

        @NotNull
        @Min(0)
        Integer bedrooms,

        @NotNull
        @Min(0)
        Integer bathrooms,

        @NotBlank
        String city,

        @NotBlank
        String country,

        @NotBlank
        String streetAddress,

        String postalCode,

        Double latitude,

        Double longitude
) {
}
