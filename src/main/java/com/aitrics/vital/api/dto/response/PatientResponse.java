package com.aitrics.vital.api.dto.response;

import com.aitrics.vital.domain.enumtype.Gender;
import com.aitrics.vital.domain.model.Patient;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;

public record PatientResponse(
    @JsonProperty("patient_id")
    String patientId,
    
    String name,
    
    Gender gender,
    
    @JsonProperty("birth_date")
    LocalDate birthDate,
    
    Long version
) {
    public static PatientResponse from(Patient patient) {
        return new PatientResponse(
            patient.getPatientId(),
            patient.getName(),
            patient.getGender(),
            patient.getBirthDate(),
            patient.getVersion()
        );
    }
}