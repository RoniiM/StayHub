package com.stayhub.service;

import com.stayhub.dto.ReviewResponse;
import com.stayhub.entity.Review;

public final class ReviewMapper {

    private ReviewMapper() {
    }

    public static ReviewResponse toResponse(Review review) {
        return new ReviewResponse(
                review.getId(),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt(),
                BookingMapper.toGuestSummary(review.getGuest()),
                BookingMapper.toPropertySummary(review.getProperty())
        );
    }
}
