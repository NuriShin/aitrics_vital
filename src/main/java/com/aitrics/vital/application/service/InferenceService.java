package com.aitrics.vital.application.service;

import com.aitrics.vital.api.dto.request.InferenceRequest;
import com.aitrics.vital.api.dto.response.InferenceResponse;
import com.aitrics.vital.domain.enumtype.VitalType;
import com.aitrics.vital.domain.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class InferenceService {
    
    private final PatientService patientService;
    
    public InferenceService(PatientService patientService) {
        this.patientService = patientService;
    }
    
    public InferenceResponse calculateVitalRisk(InferenceRequest request) {
        if (!patientService.existsById(request.patientId())) {
            throw new NotFoundException("Patient not found with ID: " + request.patientId());
        }
        
        int totalRiskConditions = 0;
        int recordCount = request.records().size();

        LocalDate evaluatedAt = LocalDate.now();
        List<String> checkedRules = new ArrayList<>();

        for (InferenceRequest.VitalRecord record : request.records()) {
            totalRiskConditions += calculateRiskConditionsForRecord(record.vitals(), checkedRules);
            evaluatedAt = LocalDate.from(record.recordedAt());
        }
        
        double riskScore = calculateRiskScore(totalRiskConditions, recordCount);

        return InferenceResponse.of(evaluatedAt, riskScore, checkedRules);
    }
    
    private int calculateRiskConditionsForRecord(Map<VitalType, Double> vitals, List<String> checkedRules) {
        int riskConditions = 0;
        
        Double hr = vitals.get(VitalType.HR);
        if (hr != null && hr > 120) {
            riskConditions++;
            checkedRules.add("HR > 120");
        }
        
        Double sbp = vitals.get(VitalType.SBP);
        if (sbp != null && sbp < 90) {
            riskConditions++;
            checkedRules.add("SBP < 90");
        }
        
        Double spo2 = vitals.get(VitalType.SpO2);
        if (spo2 != null && spo2 < 90) {
            riskConditions++;
            checkedRules.add("SpO2 < 90");
        }
        
        return riskConditions;
    }
    
    private double calculateRiskScore(int totalRiskConditions, int recordCount) {
        if (totalRiskConditions == 0) {
            return 0.3;
        } else if (totalRiskConditions <= 2) {
            return 0.4 + (totalRiskConditions - 1) * 0.15;
        } else {
            return 0.8 + Math.min(totalRiskConditions - 3, 2) * 0.1;
        }
    }
}