package com.aitrics.vital.api.controller;

import com.aitrics.vital.api.dto.request.PatientCreateRequest;
import com.aitrics.vital.api.dto.request.PatientUpdateRequest;
import com.aitrics.vital.api.dto.response.PatientResponse;
import com.aitrics.vital.application.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/patients")
@Tag(name = "환자 관리 API", description = "환자 등록 및 수정 APIs")
@SecurityRequirement(name = "bearerAuth")
public class PatientController {
    
    private final PatientService patientService;
    
    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }
    
    @PostMapping
    @Operation(summary = "환자 등록")
    public ResponseEntity<PatientResponse> createPatient(@Valid @RequestBody PatientCreateRequest request) {
        PatientResponse response = patientService.createPatient(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/{patient_id}")
    @Operation(summary = "환자 정보 수정")
    public ResponseEntity<PatientResponse> updatePatient(
            @PathVariable("patient_id") String patientId,
            @Valid @RequestBody PatientUpdateRequest request) {
        PatientResponse response = patientService.updatePatient(patientId, request);
        return ResponseEntity.ok(response);
    }

}