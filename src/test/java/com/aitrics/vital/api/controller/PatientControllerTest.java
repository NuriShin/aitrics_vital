package com.aitrics.vital.api.controller;

import com.aitrics.vital.api.dto.request.PatientCreateRequest;
import com.aitrics.vital.api.dto.response.PatientResponse;
import com.aitrics.vital.application.service.PatientService;
import com.aitrics.vital.domain.enumtype.Gender;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PatientController.class)
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PatientService patientService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void createPatient_Success() throws Exception {
        // Given
        PatientCreateRequest request = new PatientCreateRequest(
            "P00001234", "홍길동", Gender.M, LocalDate.of(1975, 3, 1)
        );
        PatientResponse response = new PatientResponse(
            "P00001234", "홍길동", Gender.M, LocalDate.of(1975, 3, 1), 0L
        );
        
        when(patientService.createPatient(any())).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/v1/patients")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.patient_id").value("P00001234"))
                .andExpect(jsonPath("$.name").value("홍길동"))
                .andExpect(jsonPath("$.gender").value("M"));
    }

    @Test
    @WithMockUser
    void getPatient_Success() throws Exception {
        // Given
        PatientResponse response = new PatientResponse(
            "P00001234", "홍길동", Gender.M, LocalDate.of(1975, 3, 1), 0L
        );
        
        when(patientService.getPatient("P00001234")).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/api/v1/patients/P00001234"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.patient_id").value("P00001234"))
                .andExpect(jsonPath("$.name").value("홍길동"));
    }
}