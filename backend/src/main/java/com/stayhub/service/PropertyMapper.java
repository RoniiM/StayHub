package com.stayhub.service;

import com.stayhub.dto.AmenityResponse;
import com.stayhub.dto.HostSummaryResponse;
import com.stayhub.dto.PropertyImageResponse;
import com.stayhub.dto.PropertyResponse;
import com.stayhub.entity.Amenity;
import com.stayhub.entity.Property;
import com.stayhub.entity.PropertyImage;
import com.stayhub.entity.User;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class PropertyMapper {

    private PropertyMapper() {
    }

    public static PropertyResponse toResponse(Property property) {
        return new PropertyResponse(
                property.getId(),
                property.getTitle(),
                property.getDescription(),
                property.getPricePerNight(),
                property.getMaxGuests(),
                property.getBedrooms(),
                property.getBathrooms(),
                property.getCity(),
                property.getCountry(),
                property.getStreetAddress(),
                property.getPostalCode(),
                property.getLatitude(),
                property.getLongitude(),
                property.getStatus(),
                property.getAverageRating(),
                property.getReviewCount(),
                toHostSummary(property.getHost()),
                toImageResponses(property.getImages()),
                toAmenityResponses(property.getAmenities()),
                property.getCreatedAt(),
                property.getUpdatedAt()
        );
    }

    public static HostSummaryResponse toHostSummary(User host) {
        return new HostSummaryResponse(
                host.getId(),
                host.getFirstName(),
                host.getLastName(),
                host.getProfileImageUrl()
        );
    }

    public static PropertyImageResponse toImageResponse(PropertyImage image) {
        return new PropertyImageResponse(image.getId(), image.getImageUrl(), image.getDisplayOrder());
    }

    public static List<PropertyImageResponse> toImageResponses(List<PropertyImage> images) {
        return images.stream()
                .map(PropertyMapper::toImageResponse)
                .collect(Collectors.toList());
    }

    public static AmenityResponse toAmenityResponse(Amenity amenity) {
        return new AmenityResponse(amenity.getId(), amenity.getName());
    }

    public static Set<AmenityResponse> toAmenityResponses(Set<Amenity> amenities) {
        return amenities.stream()
                .map(PropertyMapper::toAmenityResponse)
                .collect(Collectors.toSet());
    }
}
