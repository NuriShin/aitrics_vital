package com.aitrics.vital.api.controller;

import com.aitrics.vital.api.dto.request.InferenceRequest;
import com.aitrics.vital.api.dto.response.InferenceResponse;
import com.aitrics.vital.application.service.InferenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/inference")
@Tag(name = "Inference API", description = "단순 Rule 기반 위험 스코어")
@SecurityRequirement(name = "bearerAuth")
public class InferenceController {
    
    private final InferenceService inferenceService;
    
    public InferenceController(InferenceService inferenceService) {
        this.inferenceService = inferenceService;
    }
    
    @PostMapping("/vital-risk")
    @Operation(summary = "평가규칙에 따른 위험 스코어 확인")
    public ResponseEntity<InferenceResponse> calculateVitalRisk(@Valid @RequestBody InferenceRequest request) {
        InferenceResponse response = inferenceService.calculateVitalRisk(request);
        return ResponseEntity.ok(response);
    }
}