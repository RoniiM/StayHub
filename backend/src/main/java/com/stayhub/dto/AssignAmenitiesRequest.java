package com.stayhub.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

public record AssignAmenitiesRequest(

        @NotEmpty
        Set<Long> amenityIds
) {
}
