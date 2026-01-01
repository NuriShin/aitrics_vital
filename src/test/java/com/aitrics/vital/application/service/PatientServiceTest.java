package com.aitrics.vital.application.service;

import com.aitrics.vital.api.dto.request.PatientCreateRequest;
import com.aitrics.vital.api.dto.response.PatientResponse;
import com.aitrics.vital.domain.enumtype.Gender;
import com.aitrics.vital.domain.exception.ConflictException;
import com.aitrics.vital.domain.exception.NotFoundException;
import com.aitrics.vital.domain.model.Patient;
import com.aitrics.vital.infra.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientService patientService;

    private PatientCreateRequest createRequest;
    private Patient patient;

    @BeforeEach
    void setUp() {
        createRequest = new PatientCreateRequest(
            "P00001234",
            "홍길동",
            Gender.M,
            LocalDate.of(1975, 3, 1)
        );
        
        patient = new Patient(
            "P00001234",
            "홍길동", 
            Gender.M,
            LocalDate.of(1975, 3, 1)
        );
    }

    @Test
    void createPatient_Success() {
        // Given
        when(patientRepository.existsById(anyString())).thenReturn(false);
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        // When
        PatientResponse response = patientService.createPatient(createRequest);

        // Then
        assertThat(response.patientId()).isEqualTo("P00001234");
        assertThat(response.name()).isEqualTo("홍길동");
        assertThat(response.gender()).isEqualTo(Gender.M);
        verify(patientRepository).save(any(Patient.class));
    }

    @Test
    void createPatient_WhenPatientExists_ThrowsConflictException() {
        // Given
        when(patientRepository.existsById(anyString())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> patientService.createPatient(createRequest))
            .isInstanceOf(ConflictException.class)
            .hasMessageContaining("already exists");
    }

    @Test
    void getPatient_Success() {
        // Given
        when(patientRepository.findById(anyString())).thenReturn(Optional.of(patient));

        // When
        PatientResponse response = patientService.getPatient("P00001234");

        // Then
        assertThat(response.patientId()).isEqualTo("P00001234");
        assertThat(response.name()).isEqualTo("홍길동");
    }

    @Test
    void getPatient_WhenNotFound_ThrowsNotFoundException() {
        // Given
        when(patientRepository.findById(anyString())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> patientService.getPatient("P99999999"))
            .isInstanceOf(NotFoundException.class)
            .hasMessageContaining("not found");
    }
}