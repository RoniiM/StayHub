package com.stayhub.service;

import com.stayhub.dto.AddPropertyImageRequest;
import com.stayhub.dto.AmenityResponse;
import com.stayhub.dto.AssignAmenitiesRequest;
import com.stayhub.dto.CreatePropertyRequest;
import com.stayhub.dto.PageResponse;
import com.stayhub.dto.PropertyImageResponse;
import com.stayhub.dto.PropertyResponse;
import com.stayhub.dto.PropertySearchCriteria;
import com.stayhub.dto.UpdatePropertyRequest;
import com.stayhub.entity.enums.PropertyStatus;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PropertyService {

    PropertyResponse createProperty(Long hostId, CreatePropertyRequest request);

    PropertyResponse updateProperty(Long hostId, Long propertyId, UpdatePropertyRequest request);

    void deleteProperty(Long hostId, Long propertyId);

    PropertyResponse getPropertyById(Long propertyId);

    PageResponse<PropertyResponse> getPublishedProperties(Pageable pageable);

    PropertyResponse updateStatus(Long hostId, Long propertyId, PropertyStatus status);

    PropertyImageResponse addImage(Long hostId, Long propertyId, AddPropertyImageRequest request);

    void deleteImage(Long hostId, Long propertyId, Long imageId);

    List<PropertyImageResponse> getImages(Long propertyId);

    List<AmenityResponse> assignAmenities(Long hostId, Long propertyId, AssignAmenitiesRequest request);

    List<AmenityResponse> getAmenities(Long propertyId);

    PageResponse<PropertyResponse> searchProperties(PropertySearchCriteria criteria, Pageable pageable);
}
