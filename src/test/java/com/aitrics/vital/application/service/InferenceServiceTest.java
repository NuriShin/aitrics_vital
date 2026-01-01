package com.aitrics.vital.application.service;

import com.aitrics.vital.api.dto.request.InferenceRequest;
import com.aitrics.vital.api.dto.response.InferenceResponse;
import com.aitrics.vital.domain.enumtype.VitalType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InferenceServiceTest {

    @Mock
    private PatientService patientService;

    @InjectMocks
    private InferenceService inferenceService;

    @BeforeEach
    void setUp() {
        when(patientService.existsById(anyString())).thenReturn(true);
    }

    @Test
    void calculateVitalRisk_LowRisk() {
        // Given - Normal vitals
        InferenceRequest.VitalRecord record = new InferenceRequest.VitalRecord(
            LocalDateTime.now(),
            Map.of(
                VitalType.HR, 80.0,
                VitalType.SBP, 120.0,
                VitalType.SpO2, 98.0
            )
        );
        InferenceRequest request = new InferenceRequest("P00001234", List.of(record));

        // When
        InferenceResponse response = inferenceService.calculateVitalRisk(request);

        // Then
        assertThat(response.riskScore()).isEqualTo(0.3);
        assertThat(response.riskLevel()).isEqualTo("LOW");
    }

    @Test
    void calculateVitalRisk_HighRisk() {
        // Given - All risk conditions met
        InferenceRequest.VitalRecord record = new InferenceRequest.VitalRecord(
            LocalDateTime.now(),
            Map.of(
                VitalType.HR, 130.0,  // > 120 (risk condition)
                VitalType.SBP, 85.0,  // < 90 (risk condition) 
                VitalType.SpO2, 89.0  // < 90 (risk condition)
            )
        );
        InferenceRequest request = new InferenceRequest("P00001234", List.of(record));

        // When
        InferenceResponse response = inferenceService.calculateVitalRisk(request);

        // Then
        assertThat(response.riskScore()).isGreaterThanOrEqualTo(0.8);
        assertThat(response.riskLevel()).isEqualTo("HIGH");
    }

    @Test
    void calculateVitalRisk_MediumRisk() {
        // Given - One risk condition met
        InferenceRequest.VitalRecord record = new InferenceRequest.VitalRecord(
            LocalDateTime.now(),
            Map.of(
                VitalType.HR, 130.0,  // > 120 (risk condition)
                VitalType.SBP, 120.0, // Normal
                VitalType.SpO2, 98.0  // Normal
            )
        );
        InferenceRequest request = new InferenceRequest("P00001234", List.of(record));

        // When
        InferenceResponse response = inferenceService.calculateVitalRisk(request);

        // Then
        assertThat(response.riskScore()).isBetween(0.4, 0.7);
        assertThat(response.riskLevel()).isEqualTo("MEDIUM");
    }
}