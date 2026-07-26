package com.stayhub.dto;

public record HostSummaryResponse(
        Long id,
        String firstName,
        String lastName,
        String profileImageUrl
) {
}
