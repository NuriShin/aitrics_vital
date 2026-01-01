package com.aitrics.vital.api.dto.response;

import com.aitrics.vital.domain.enumtype.VitalType;
import com.aitrics.vital.domain.model.Vital;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public record VitalResponse(
    @JsonProperty("vital_id")
    Long vitalId,
    
    @JsonProperty("patient_id")
    String patientId,
    
    @JsonProperty("recorded_at")
    LocalDateTime recordedAt,
    
    @JsonProperty("vital_type")
    VitalType vitalType,
    
    Double value,
    
    Long version
) {
    public static VitalResponse from(Vital vital) {
        return new VitalResponse(
            vital.getVitalId(),
            vital.getPatientId(),
            vital.getRecordedAt(),
            vital.getVitalType(),
            vital.getValue(),
            vital.getVersion()
        );
    }
}