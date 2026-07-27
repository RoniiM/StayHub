package com.stayhub.mapper;

import com.stayhub.dto.HostSummaryResponse;
import com.stayhub.dto.PropertyResponse;
import com.stayhub.dto.UpdatePropertyRequest;
import com.stayhub.entity.Property;
import com.stayhub.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {PropertyImageMapper.class, AmenityMapper.class})
public interface PropertyMapper {

    PropertyResponse toResponse(Property property);

    HostSummaryResponse toHostSummary(User host);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "averageRating", ignore = true)
    @Mapping(target = "reviewCount", ignore = true)
    @Mapping(target = "host", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "bookings", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "amenities", ignore = true)
    void updatePropertyFromRequest(UpdatePropertyRequest request, @MappingTarget Property property);
}
