package com.vital.api.dto.request;

import jakarta.validation.constraints.NotNull;

public record VitalUpdateRequest(
    @NotNull
    Double value,
    
    @NotNull
    Long version
) {}