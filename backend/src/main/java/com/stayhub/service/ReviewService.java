package com.stayhub.service;

import com.stayhub.dto.CreateReviewRequest;
import com.stayhub.dto.PageResponse;
import com.stayhub.dto.ReviewResponse;
import com.stayhub.dto.UpdateReviewRequest;
import org.springframework.data.domain.Pageable;

public interface ReviewService {

    ReviewResponse createReview(Long guestId, CreateReviewRequest request);

    ReviewResponse updateReview(Long guestId, Long reviewId, UpdateReviewRequest request);

    void deleteReview(Long guestId, Long reviewId);

    ReviewResponse getReviewById(Long reviewId);

    PageResponse<ReviewResponse> getPropertyReviews(Long propertyId, Pageable pageable);
}
