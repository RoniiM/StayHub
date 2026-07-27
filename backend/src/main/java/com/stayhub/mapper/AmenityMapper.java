package com.stayhub.mapper;

import com.stayhub.dto.AmenityResponse;
import com.stayhub.entity.Amenity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AmenityMapper {

    AmenityResponse toResponse(Amenity amenity);
}
