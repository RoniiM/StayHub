package com.stayhub.controller;

import com.stayhub.dto.AmenityResponse;
import com.stayhub.dto.PageResponse;
import com.stayhub.dto.PropertyImageResponse;
import com.stayhub.dto.PropertyResponse;
import com.stayhub.dto.PropertySearchCriteria;
import com.stayhub.service.PropertyService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/properties")
public class PropertyController {

    private final PropertyService propertyService;

    public PropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @GetMapping("/{propertyId}")
    public ResponseEntity<PropertyResponse> getPropertyById(@PathVariable Long propertyId) {
        return ResponseEntity.ok(propertyService.getPropertyById(propertyId));
    }

    @GetMapping
    public ResponseEntity<PageResponse<PropertyResponse>> getPublishedProperties(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(propertyService.getPublishedProperties(pageable));
    }

    @GetMapping("/{propertyId}/images")
    public ResponseEntity<List<PropertyImageResponse>> getImages(@PathVariable Long propertyId) {
        return ResponseEntity.ok(propertyService.getImages(propertyId));
    }

    @GetMapping("/{propertyId}/amenities")
    public ResponseEntity<List<AmenityResponse>> getAmenities(@PathVariable Long propertyId) {
        return ResponseEntity.ok(propertyService.getAmenities(propertyId));
    }

    @GetMapping("/search")
    public ResponseEntity<PageResponse<PropertyResponse>> searchProperties(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Integer guests,
            @RequestParam(required = false) Integer bedrooms,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        PropertySearchCriteria criteria = new PropertySearchCriteria(city, country, minPrice, maxPrice, guests, bedrooms);
        return ResponseEntity.ok(propertyService.searchProperties(criteria, pageable));
    }
}
