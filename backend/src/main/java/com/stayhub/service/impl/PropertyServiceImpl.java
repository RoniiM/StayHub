package com.stayhub.service.impl;

import com.stayhub.dto.AddPropertyImageRequest;
import com.stayhub.dto.AmenityResponse;
import com.stayhub.dto.AssignAmenitiesRequest;
import com.stayhub.dto.CreatePropertyRequest;
import com.stayhub.dto.PageResponse;
import com.stayhub.dto.PropertyImageResponse;
import com.stayhub.dto.PropertyResponse;
import com.stayhub.dto.PropertySearchCriteria;
import com.stayhub.dto.UpdatePropertyRequest;
import com.stayhub.entity.Amenity;
import com.stayhub.entity.Property;
import com.stayhub.entity.PropertyImage;
import com.stayhub.entity.User;
import com.stayhub.entity.enums.PropertyStatus;
import com.stayhub.exception.PropertyOwnershipException;
import com.stayhub.exception.ResourceNotFoundException;
import com.stayhub.repository.AmenityRepository;
import com.stayhub.repository.PropertyRepository;
import com.stayhub.repository.PropertySpecifications;
import com.stayhub.repository.UserRepository;
import com.stayhub.service.PropertyMapper;
import com.stayhub.service.PropertyService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class PropertyServiceImpl implements PropertyService {

    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final AmenityRepository amenityRepository;

    public PropertyServiceImpl(PropertyRepository propertyRepository,
                                UserRepository userRepository,
                                AmenityRepository amenityRepository) {
        this.propertyRepository = propertyRepository;
        this.userRepository = userRepository;
        this.amenityRepository = amenityRepository;
    }

    @Override
    public PropertyResponse createProperty(Long hostId, CreatePropertyRequest request) {
        User host = getHostOrThrow(hostId);

        Property property = Property.builder()
                .title(request.title())
                .description(request.description())
                .pricePerNight(request.pricePerNight())
                .maxGuests(request.maxGuests())
                .bedrooms(request.bedrooms())
                .bathrooms(request.bathrooms())
                .city(request.city())
                .country(request.country())
                .streetAddress(request.streetAddress())
                .postalCode(request.postalCode())
                .latitude(request.latitude())
                .longitude(request.longitude())
                .status(PropertyStatus.DRAFT)
                .host(host)
                .build();

        Property saved = propertyRepository.save(property);
        return PropertyMapper.toResponse(saved);
    }

    @Override
    public PropertyResponse updateProperty(Long hostId, Long propertyId, UpdatePropertyRequest request) {
        Property property = getOwnedPropertyOrThrow(hostId, propertyId);

        property.setTitle(request.title());
        property.setDescription(request.description());
        property.setPricePerNight(request.pricePerNight());
        property.setMaxGuests(request.maxGuests());
        property.setBedrooms(request.bedrooms());
        property.setBathrooms(request.bathrooms());
        property.setCity(request.city());
        property.setCountry(request.country());
        property.setStreetAddress(request.streetAddress());
        property.setPostalCode(request.postalCode());
        property.setLatitude(request.latitude());
        property.setLongitude(request.longitude());

        return PropertyMapper.toResponse(property);
    }

    @Override
    public void deleteProperty(Long hostId, Long propertyId) {
        Property property = getOwnedPropertyOrThrow(hostId, propertyId);
        propertyRepository.delete(property);
    }

    @Override
    @Transactional(readOnly = true)
    public PropertyResponse getPropertyById(Long propertyId) {
        return PropertyMapper.toResponse(getPropertyOrThrow(propertyId));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<PropertyResponse> getPublishedProperties(Pageable pageable) {
        Page<Property> page = propertyRepository.findByStatus(PropertyStatus.PUBLISHED, pageable);
        return PageResponse.from(page.map(PropertyMapper::toResponse));
    }

    @Override
    public PropertyResponse updateStatus(Long hostId, Long propertyId, PropertyStatus status) {
        Property property = getOwnedPropertyOrThrow(hostId, propertyId);
        property.setStatus(status);
        return PropertyMapper.toResponse(property);
    }

    @Override
    public PropertyImageResponse addImage(Long hostId, Long propertyId, AddPropertyImageRequest request) {
        Property property = getOwnedPropertyOrThrow(hostId, propertyId);

        PropertyImage image = PropertyImage.builder()
                .imageUrl(request.imageUrl())
                .displayOrder(request.displayOrder())
                .property(property)
                .build();

        property.getImages().add(image);

        return PropertyMapper.toImageResponse(image);
    }

    @Override
    public void deleteImage(Long hostId, Long propertyId, Long imageId) {
        Property property = getOwnedPropertyOrThrow(hostId, propertyId);

        PropertyImage image = property.getImages().stream()
                .filter(img -> img.getId().equals(imageId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Image not found with id: " + imageId + " for property: " + propertyId));

        property.getImages().remove(image);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PropertyImageResponse> getImages(Long propertyId) {
        Property property = getPropertyOrThrow(propertyId);
        return PropertyMapper.toImageResponses(property.getImages());
    }

    @Override
    public List<AmenityResponse> assignAmenities(Long hostId, Long propertyId, AssignAmenitiesRequest request) {
        Property property = getOwnedPropertyOrThrow(hostId, propertyId);

        List<Amenity> amenities = amenityRepository.findAllById(request.amenityIds());
        if (amenities.size() != request.amenityIds().size()) {
            Set<Long> foundIds = amenities.stream().map(Amenity::getId).collect(Collectors.toSet());
            Set<Long> missingIds = new HashSet<>(request.amenityIds());
            missingIds.removeAll(foundIds);
            throw new ResourceNotFoundException("Amenities not found with ids: " + missingIds);
        }

        property.setAmenities(new HashSet<>(amenities));

        return amenities.stream()
                .map(PropertyMapper::toAmenityResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AmenityResponse> getAmenities(Long propertyId) {
        Property property = getPropertyOrThrow(propertyId);
        return property.getAmenities().stream()
                .map(PropertyMapper::toAmenityResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<PropertyResponse> searchProperties(PropertySearchCriteria criteria, Pageable pageable) {
        Page<Property> page = propertyRepository.findAll(PropertySpecifications.search(criteria), pageable);
        return PageResponse.from(page.map(PropertyMapper::toResponse));
    }

    private User getHostOrThrow(Long hostId) {
        return userRepository.findById(hostId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + hostId));
    }

    private Property getPropertyOrThrow(Long propertyId) {
        return propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + propertyId));
    }

    private Property getOwnedPropertyOrThrow(Long hostId, Long propertyId) {
        Property property = getPropertyOrThrow(propertyId);
        if (!property.getHost().getId().equals(hostId)) {
            throw new PropertyOwnershipException(
                    "Host with id: " + hostId + " does not own property with id: " + propertyId);
        }
        return property;
    }
}
