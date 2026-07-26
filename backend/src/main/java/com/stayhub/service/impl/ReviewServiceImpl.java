package com.stayhub.service.impl;

import com.stayhub.dto.CreateReviewRequest;
import com.stayhub.dto.PageResponse;
import com.stayhub.dto.ReviewResponse;
import com.stayhub.dto.UpdateReviewRequest;
import com.stayhub.entity.Booking;
import com.stayhub.entity.Property;
import com.stayhub.entity.Review;
import com.stayhub.entity.User;
import com.stayhub.entity.enums.BookingStatus;
import com.stayhub.exception.BookingOwnershipException;
import com.stayhub.exception.DuplicateReviewException;
import com.stayhub.exception.InvalidBookingRequestException;
import com.stayhub.exception.ResourceNotFoundException;
import com.stayhub.exception.ReviewOwnershipException;
import com.stayhub.repository.BookingRepository;
import com.stayhub.repository.PropertyRepository;
import com.stayhub.repository.ReviewRepository;
import com.stayhub.repository.UserRepository;
import com.stayhub.service.ReviewMapper;
import com.stayhub.service.ReviewService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository,
                              BookingRepository bookingRepository,
                              UserRepository userRepository,
                              PropertyRepository propertyRepository) {
        this.reviewRepository = reviewRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.propertyRepository = propertyRepository;
    }

    @Override
    public ReviewResponse createReview(Long guestId, CreateReviewRequest request) {
        User guest = getUserOrThrow(guestId);
        Booking booking = bookingRepository.findById(request.bookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + request.bookingId()));

        if (!booking.getGuest().getId().equals(guestId)) {
            throw new BookingOwnershipException(
                    "Guest with id: " + guestId + " does not own booking with id: " + booking.getId());
        }

        if (booking.getStatus() != BookingStatus.COMPLETED) {
            throw new InvalidBookingRequestException("Only completed bookings can be reviewed");
        }

        if (reviewRepository.findByBookingId(booking.getId()).isPresent()) {
            throw new DuplicateReviewException("Booking with id: " + booking.getId() + " already has a review");
        }

        Review review = Review.builder()
                .rating(request.rating())
                .comment(request.comment())
                .guest(guest)
                .property(booking.getProperty())
                .booking(booking)
                .build();

        Review saved = reviewRepository.save(review);
        recalculatePropertyRating(booking.getProperty());

        return ReviewMapper.toResponse(saved);
    }

    @Override
    public ReviewResponse updateReview(Long guestId, Long reviewId, UpdateReviewRequest request) {
        Review review = getReviewOrThrow(reviewId);
        validateReviewOwnership(guestId, review);

        review.setRating(request.rating());
        review.setComment(request.comment());

        recalculatePropertyRating(review.getProperty());

        return ReviewMapper.toResponse(review);
    }

    @Override
    public void deleteReview(Long guestId, Long reviewId) {
        Review review = getReviewOrThrow(reviewId);
        validateReviewOwnership(guestId, review);

        Property property = review.getProperty();
        reviewRepository.delete(review);

        recalculatePropertyRating(property);
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewResponse getReviewById(Long reviewId) {
        return ReviewMapper.toResponse(getReviewOrThrow(reviewId));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ReviewResponse> getPropertyReviews(Long propertyId, Pageable pageable) {
        if (!propertyRepository.existsById(propertyId)) {
            throw new ResourceNotFoundException("Property not found with id: " + propertyId);
        }
        Page<Review> page = reviewRepository.findByPropertyId(propertyId, pageable);
        return PageResponse.from(page.map(ReviewMapper::toResponse));
    }

    private void recalculatePropertyRating(Property property) {
        reviewRepository.flush();
        long count = reviewRepository.countByPropertyId(property.getId());
        Double average = reviewRepository.findAverageRatingByPropertyId(property.getId());
        property.setReviewCount((int) count);
        property.setAverageRating(average != null ? average : 0.0);
    }

    private void validateReviewOwnership(Long guestId, Review review) {
        if (!review.getGuest().getId().equals(guestId)) {
            throw new ReviewOwnershipException(
                    "Guest with id: " + guestId + " does not own review with id: " + review.getId());
        }
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    private Review getReviewOrThrow(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));
    }
}
