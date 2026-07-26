package com.stayhub.repository;

import com.stayhub.dto.PropertySearchCriteria;
import com.stayhub.entity.Property;
import com.stayhub.entity.enums.PropertyStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class PropertySpecifications {

    private PropertySpecifications() {
    }

    public static Specification<Property> search(PropertySearchCriteria criteria) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("status"), PropertyStatus.PUBLISHED));

            if (criteria.city() != null && !criteria.city().isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("city")), criteria.city().toLowerCase()));
            }
            if (criteria.country() != null && !criteria.country().isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("country")), criteria.country().toLowerCase()));
            }
            if (criteria.minPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("pricePerNight"), criteria.minPrice()));
            }
            if (criteria.maxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("pricePerNight"), criteria.maxPrice()));
            }
            if (criteria.guests() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("maxGuests"), criteria.guests()));
            }
            if (criteria.bedrooms() != null) {
                predicates.add(cb.equal(root.get("bedrooms"), criteria.bedrooms()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
