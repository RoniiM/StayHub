package com.stayhub.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType
) {
}
