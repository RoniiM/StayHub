package com.stayhub.dto;

public record GuestSummaryResponse(
        Long id,
        String firstName,
        String lastName,
        String profileImageUrl
) {
}
