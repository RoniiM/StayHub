package com.stayhub.mapper;

import com.stayhub.dto.BookingResponse;
import com.stayhub.dto.GuestSummaryResponse;
import com.stayhub.dto.PropertySummaryResponse;
import com.stayhub.entity.Booking;
import com.stayhub.entity.Property;
import com.stayhub.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    @Mapping(target = "bookingId", source = "id")
    BookingResponse toResponse(Booking booking);

    PropertySummaryResponse toPropertySummary(Property property);

    GuestSummaryResponse toGuestSummary(User guest);
}
