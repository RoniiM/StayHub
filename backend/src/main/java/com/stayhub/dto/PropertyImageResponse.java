package com.stayhub.dto;

public record PropertyImageResponse(
        Long id,
        String imageUrl,
        Integer displayOrder
) {
}
