package com.stayhub.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddPropertyImageRequest(

        @NotBlank
        String imageUrl,

        @NotNull
        @Min(0)
        Integer displayOrder
) {
}
