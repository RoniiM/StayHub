package com.stayhub.repository;

import com.stayhub.entity.Booking;
import com.stayhub.entity.enums.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    Page<Booking> findByGuestId(Long guestId, Pageable pageable);

    Page<Booking> findByPropertyHostId(Long hostId, Pageable pageable);

    List<Booking> findByStatusAndCheckOutBefore(BookingStatus status, LocalDate date);

    @Query("SELECT b FROM Booking b WHERE b.property.id = :propertyId AND b.status IN :statuses "
            + "AND b.checkIn < :checkOut AND b.checkOut > :checkIn")
    List<Booking> findOverlappingBookings(@Param("propertyId") Long propertyId,
                                           @Param("checkIn") LocalDate checkIn,
                                           @Param("checkOut") LocalDate checkOut,
                                           @Param("statuses") List<BookingStatus> statuses);
}
