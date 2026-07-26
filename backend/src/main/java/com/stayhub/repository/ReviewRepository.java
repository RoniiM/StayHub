package com.stayhub.repository;

import com.stayhub.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findByPropertyId(Long propertyId, Pageable pageable);

    List<Review> findByGuestId(Long guestId);

    Optional<Review> findByBookingId(Long bookingId);

    long countByPropertyId(Long propertyId);

    @Query("SELECT COALESCE(AVG(r.rating), 0) FROM Review r WHERE r.property.id = :propertyId")
    Double findAverageRatingByPropertyId(@Param("propertyId") Long propertyId);
}
