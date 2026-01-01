package com.aitrics.vital.api.dto.request;

import com.aitrics.vital.domain.enumtype.Gender;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record PatientUpdateRequest(
    @NotBlank
    String name,
    
    @NotNull
    Gender gender,
    
    @JsonProperty("birth_date")
    @NotNull
    LocalDate birthDate,
    
    @NotNull
    Long version
) {}