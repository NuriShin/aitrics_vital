package com.aitrics.vital.api.controller;

import com.aitrics.vital.api.dto.request.VitalCreateRequest;
import com.aitrics.vital.api.dto.request.VitalUpdateRequest;
import com.aitrics.vital.api.dto.response.VitalQueryResponse;
import com.aitrics.vital.api.dto.response.VitalResponse;
import com.aitrics.vital.application.service.VitalService;
import com.aitrics.vital.domain.enumtype.VitalType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Vital 데이터 API", description = "Vital data APIs")
@SecurityRequirement(name = "bearerAuth")
public class VitalController {
    
    private final VitalService vitalService;
    
    public VitalController(VitalService vitalService) {
        this.vitalService = vitalService;
    }
    
    @PostMapping("/vitals")
    @Operation(summary = "Vital 데이터 저장")
    public ResponseEntity<VitalResponse> createVital(@Valid @RequestBody VitalCreateRequest request) {
        VitalResponse response = vitalService.createVital(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/patients/{patient_id}/vitals")
    @Operation(summary = "Vital 데이터 조회")
    public ResponseEntity<VitalQueryResponse> getVitals(
            @PathVariable("patient_id") String patientId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(required = false) VitalType vital_type) {
        VitalQueryResponse response = vitalService.getVitals(patientId, from, to, vital_type);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/vitals/{vital_id}")
    @Operation(summary = "Vital 데이터 수정")
    public ResponseEntity<VitalResponse> updateVital(
            @PathVariable("vital_id") Long vitalId,
            @Valid @RequestBody VitalUpdateRequest request) {
        VitalResponse response = vitalService.updateVital(vitalId, request);
        return ResponseEntity.ok(response);
    }
}