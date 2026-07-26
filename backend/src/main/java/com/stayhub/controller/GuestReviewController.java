package com.stayhub.controller;

import com.stayhub.dto.CreateReviewRequest;
import com.stayhub.dto.ReviewResponse;
import com.stayhub.dto.UpdateReviewRequest;
import com.stayhub.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/guests/{guestId}/reviews")
public class GuestReviewController {

    private final ReviewService reviewService;

    public GuestReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(@PathVariable Long guestId,
                                                        @Valid @RequestBody CreateReviewRequest request) {
        ReviewResponse response = reviewService.createReview(guestId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> updateReview(@PathVariable Long guestId,
                                                        @PathVariable Long reviewId,
                                                        @Valid @RequestBody UpdateReviewRequest request) {
        return ResponseEntity.ok(reviewService.updateReview(guestId, reviewId, request));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long guestId, @PathVariable Long reviewId) {
        reviewService.deleteReview(guestId, reviewId);
        return ResponseEntity.noContent().build();
    }
}
