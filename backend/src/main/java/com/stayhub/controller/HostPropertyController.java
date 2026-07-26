package com.stayhub.controller;

import com.stayhub.dto.AddPropertyImageRequest;
import com.stayhub.dto.AmenityResponse;
import com.stayhub.dto.AssignAmenitiesRequest;
import com.stayhub.dto.CreatePropertyRequest;
import com.stayhub.dto.PropertyImageResponse;
import com.stayhub.dto.PropertyResponse;
import com.stayhub.dto.UpdatePropertyRequest;
import com.stayhub.dto.UpdatePropertyStatusRequest;
import com.stayhub.service.PropertyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/hosts/{hostId}/properties")
public class HostPropertyController {

    private final PropertyService propertyService;

    public HostPropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @PostMapping
    public ResponseEntity<PropertyResponse> createProperty(@PathVariable Long hostId,
                                                             @Valid @RequestBody CreatePropertyRequest request) {
        PropertyResponse response = propertyService.createProperty(hostId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{propertyId}")
    public ResponseEntity<PropertyResponse> updateProperty(@PathVariable Long hostId,
                                                             @PathVariable Long propertyId,
                                                             @Valid @RequestBody UpdatePropertyRequest request) {
        return ResponseEntity.ok(propertyService.updateProperty(hostId, propertyId, request));
    }

    @DeleteMapping("/{propertyId}")
    public ResponseEntity<Void> deleteProperty(@PathVariable Long hostId, @PathVariable Long propertyId) {
        propertyService.deleteProperty(hostId, propertyId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{propertyId}/status")
    public ResponseEntity<PropertyResponse> updateStatus(@PathVariable Long hostId,
                                                           @PathVariable Long propertyId,
                                                           @Valid @RequestBody UpdatePropertyStatusRequest request) {
        return ResponseEntity.ok(propertyService.updateStatus(hostId, propertyId, request.status()));
    }

    @PostMapping("/{propertyId}/images")
    public ResponseEntity<PropertyImageResponse> addImage(@PathVariable Long hostId,
                                                            @PathVariable Long propertyId,
                                                            @Valid @RequestBody AddPropertyImageRequest request) {
        PropertyImageResponse response = propertyService.addImage(hostId, propertyId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{propertyId}/images/{imageId}")
    public ResponseEntity<Void> deleteImage(@PathVariable Long hostId,
                                             @PathVariable Long propertyId,
                                             @PathVariable Long imageId) {
        propertyService.deleteImage(hostId, propertyId, imageId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{propertyId}/amenities")
    public ResponseEntity<List<AmenityResponse>> assignAmenities(@PathVariable Long hostId,
                                                                   @PathVariable Long propertyId,
                                                                   @Valid @RequestBody AssignAmenitiesRequest request) {
        return ResponseEntity.ok(propertyService.assignAmenities(hostId, propertyId, request));
    }
}
