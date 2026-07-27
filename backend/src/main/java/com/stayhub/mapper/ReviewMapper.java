package com.stayhub.mapper;

import com.stayhub.dto.ReviewResponse;
import com.stayhub.dto.UpdateReviewRequest;
import com.stayhub.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = BookingMapper.class)
public interface ReviewMapper {

    @Mapping(target = "reviewId", source = "id")
    ReviewResponse toResponse(Review review);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "guest", ignore = true)
    @Mapping(target = "property", ignore = true)
    @Mapping(target = "booking", ignore = true)
    void updateReviewFromRequest(UpdateReviewRequest request, @MappingTarget Review review);
}
