package com.stayhub.dto;

import com.stayhub.entity.enums.PropertyStatus;
import jakarta.validation.constraints.NotNull;

public record UpdatePropertyStatusRequest(

        @NotNull
        PropertyStatus status
) {
}
