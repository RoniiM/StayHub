package com.stayhub.controller;

import com.stayhub.dto.AddPropertyImageRequest;
import com.stayhub.dto.AmenityResponse;
import com.stayhub.dto.AssignAmenitiesRequest;
import com.stayhub.dto.CreatePropertyRequest;
import com.stayhub.dto.PageResponse;
import com.stayhub.dto.PropertyImageResponse;
import com.stayhub.dto.PropertyResponse;
import com.stayhub.dto.PropertySearchCriteria;
import com.stayhub.dto.ReviewResponse;
import com.stayhub.dto.UpdatePropertyRequest;
import com.stayhub.dto.UpdatePropertyStatusRequest;
import com.stayhub.entity.User;
import com.stayhub.exception.ErrorResponse;
import com.stayhub.security.CurrentUser;
import com.stayhub.service.PropertyService;
import com.stayhub.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/properties")
@Tag(name = "Properties", description = "Property listing, search, and host-managed property administration.")
public class PropertyController {

    private final PropertyService propertyService;
    private final ReviewService reviewService;

    public PropertyController(PropertyService propertyService, ReviewService reviewService) {
        this.propertyService = propertyService;
        this.reviewService = reviewService;
    }

    @Operation(summary = "Get property details", description = "Returns full details for a single property. Public endpoint.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Property found"),
            @ApiResponse(responseCode = "404", description = "Property not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{propertyId}")
    public ResponseEntity<PropertyResponse> getPropertyById(
            @Parameter(description = "ID of the property to retrieve") @PathVariable Long propertyId) {
        return ResponseEntity.ok(propertyService.getPropertyById(propertyId));
    }

    @Operation(summary = "List published properties", description = "Returns a paginated list of published properties. Public endpoint.")
    @ApiResponse(responseCode = "200", description = "Page of published properties")
    @GetMapping
    public ResponseEntity<PageResponse<PropertyResponse>> getPublishedProperties(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(propertyService.getPublishedProperties(pageable));
    }

    @Operation(summary = "Search published properties",
            description = "Searches published properties with optional filters, sorting, and pagination. Public endpoint.")
    @ApiResponse(responseCode = "200", description = "Page of matching properties")
    @GetMapping("/search")
    public ResponseEntity<PageResponse<PropertyResponse>> searchProperties(
            @Parameter(description = "Filter by city") @RequestParam(required = false) String city,
            @Parameter(description = "Filter by country") @RequestParam(required = false) String country,
            @Parameter(description = "Minimum price per night") @RequestParam(required = false) BigDecimal minPrice,
            @Parameter(description = "Maximum price per night") @RequestParam(required = false) BigDecimal maxPrice,
            @Parameter(description = "Minimum number of guests the property must accommodate") @RequestParam(required = false) Integer guests,
            @Parameter(description = "Exact number of bedrooms") @RequestParam(required = false) Integer bedrooms,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        PropertySearchCriteria criteria = new PropertySearchCriteria(city, country, minPrice, maxPrice, guests, bedrooms);
        return ResponseEntity.ok(propertyService.searchProperties(criteria, pageable));
    }

    @Operation(summary = "Get property images", description = "Returns the images belonging to a property. Requires authentication.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Images retrieved"),
            @ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Property not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{propertyId}/images")
    public ResponseEntity<List<PropertyImageResponse>> getImages(
            @Parameter(description = "ID of the property") @PathVariable Long propertyId) {
        return ResponseEntity.ok(propertyService.getImages(propertyId));
    }

    @Operation(summary = "Get property amenities", description = "Returns the amenities assigned to a property. Requires authentication.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Amenities retrieved"),
            @ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Property not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{propertyId}/amenities")
    public ResponseEntity<List<AmenityResponse>> getAmenities(
            @Parameter(description = "ID of the property") @PathVariable Long propertyId) {
        return ResponseEntity.ok(propertyService.getAmenities(propertyId));
    }

    @Operation(summary = "Get property reviews", description = "Returns a paginated list of reviews for a property, newest first. Public endpoint.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page of reviews"),
            @ApiResponse(responseCode = "404", description = "Property not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{propertyId}/reviews")
    public ResponseEntity<PageResponse<ReviewResponse>> getPropertyReviews(
            @Parameter(description = "ID of the property") @PathVariable Long propertyId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(reviewService.getPropertyReviews(propertyId, pageable));
    }

    @Operation(summary = "Create a property", description = "Creates a new property owned by the authenticated host. Starts in DRAFT status.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Property created"),
            @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Caller does not have the HOST role", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('HOST')")
    @PostMapping
    public ResponseEntity<PropertyResponse> createProperty(@CurrentUser User currentUser,
                                                             @Valid @RequestBody CreatePropertyRequest request) {
        PropertyResponse response = propertyService.createProperty(currentUser.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Update a property", description = "Updates the editable fields of a property owned by the authenticated host.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Property updated"),
            @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Caller does not own this property", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Property not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('HOST')")
    @PutMapping("/{propertyId}")
    public ResponseEntity<PropertyResponse> updateProperty(@CurrentUser User currentUser,
                                                             @Parameter(description = "ID of the property to update") @PathVariable Long propertyId,
                                                             @Valid @RequestBody UpdatePropertyRequest request) {
        return ResponseEntity.ok(propertyService.updateProperty(currentUser.getId(), propertyId, request));
    }

    @Operation(summary = "Delete a property", description = "Deletes a property owned by the authenticated host, along with its images.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Property deleted"),
            @ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Caller does not own this property", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Property not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('HOST')")
    @DeleteMapping("/{propertyId}")
    public ResponseEntity<Void> deleteProperty(@CurrentUser User currentUser,
                                                @Parameter(description = "ID of the property to delete") @PathVariable Long propertyId) {
        propertyService.deleteProperty(currentUser.getId(), propertyId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Change property status", description = "Transitions a property between DRAFT, PUBLISHED, and ARCHIVED.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status updated"),
            @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Caller does not own this property", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Property not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('HOST')")
    @PatchMapping("/{propertyId}/status")
    public ResponseEntity<PropertyResponse> updateStatus(@CurrentUser User currentUser,
                                                           @Parameter(description = "ID of the property") @PathVariable Long propertyId,
                                                           @Valid @RequestBody UpdatePropertyStatusRequest request) {
        return ResponseEntity.ok(propertyService.updateStatus(currentUser.getId(), propertyId, request.status()));
    }

    @Operation(summary = "Add a property image", description = "Adds an image (by URL) to a property owned by the authenticated host.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Image added"),
            @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Caller does not own this property", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Property not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('HOST')")
    @PostMapping("/{propertyId}/images")
    public ResponseEntity<PropertyImageResponse> addImage(@CurrentUser User currentUser,
                                                            @Parameter(description = "ID of the property") @PathVariable Long propertyId,
                                                            @Valid @RequestBody AddPropertyImageRequest request) {
        PropertyImageResponse response = propertyService.addImage(currentUser.getId(), propertyId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Delete a property image", description = "Removes an image from a property owned by the authenticated host.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Image deleted"),
            @ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Caller does not own this property", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Property or image not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('HOST')")
    @DeleteMapping("/{propertyId}/images/{imageId}")
    public ResponseEntity<Void> deleteImage(@CurrentUser User currentUser,
                                             @Parameter(description = "ID of the property") @PathVariable Long propertyId,
                                             @Parameter(description = "ID of the image to delete") @PathVariable Long imageId) {
        propertyService.deleteImage(currentUser.getId(), propertyId, imageId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Assign amenities to a property",
            description = "Replaces a property's amenity set with the given list of existing amenity IDs.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Amenities assigned"),
            @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Caller does not own this property", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Property or amenity not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('HOST')")
    @PutMapping("/{propertyId}/amenities")
    public ResponseEntity<List<AmenityResponse>> assignAmenities(@CurrentUser User currentUser,
                                                                   @Parameter(description = "ID of the property") @PathVariable Long propertyId,
                                                                   @Valid @RequestBody AssignAmenitiesRequest request) {
        return ResponseEntity.ok(propertyService.assignAmenities(currentUser.getId(), propertyId, request));
    }
}
