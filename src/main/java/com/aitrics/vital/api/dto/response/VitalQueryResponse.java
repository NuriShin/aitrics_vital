package com.aitrics.vital.api.dto.response;

import com.aitrics.vital.domain.enumtype.VitalType;
import com.aitrics.vital.domain.model.Vital;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;

public record VitalQueryResponse(
    @JsonProperty("patient_id")
    String patientId,
    
    @JsonProperty("vital_type")
    VitalType vitalType,
    
    List<VitalItem> items
) {
    public record VitalItem(
        @JsonProperty("recorded_at")
        LocalDateTime recordedAt,
        
        Double value
    ) {}
    
    public static VitalQueryResponse from(String patientId, VitalType vitalType, List<Vital> vitals) {
        List<VitalItem> items = vitals.stream()
            .map(vital -> new VitalItem(vital.getRecordedAt(), vital.getValue()))
            .toList();
        
        return new VitalQueryResponse(patientId, vitalType, items);
    }
}