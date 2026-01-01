package com.aitrics.vital.api.dto.request;

import com.aitrics.vital.domain.enumtype.VitalType;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record VitalCreateRequest(
    @JsonProperty("patient_id")
    @NotBlank
    String patientId,
    
    @JsonProperty("recorded_at")
    @NotNull
    LocalDateTime recordedAt,
    
    @JsonProperty("vital_type")
    @NotNull
    VitalType vitalType,

    @JsonProperty("vital_value")
    @NotNull
    Double value
) {}