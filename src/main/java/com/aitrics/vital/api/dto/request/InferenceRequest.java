package com.aitrics.vital.api.dto.request;

import com.aitrics.vital.domain.enumtype.VitalType;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record InferenceRequest(
    @JsonProperty("patient_id")
    @NotBlank
    String patientId,
    
    @NotEmpty
    @Valid
    List<VitalRecord> records
) {
    public record VitalRecord(
        @JsonProperty("recorded_at")
        @NotNull
        LocalDateTime recordedAt,
        
        @NotEmpty
        Map<VitalType, Double> vitals
    ) {}
}