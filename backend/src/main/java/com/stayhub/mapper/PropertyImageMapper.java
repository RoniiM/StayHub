package com.stayhub.mapper;

import com.stayhub.dto.PropertyImageResponse;
import com.stayhub.entity.PropertyImage;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PropertyImageMapper {

    PropertyImageResponse toResponse(PropertyImage image);
}
