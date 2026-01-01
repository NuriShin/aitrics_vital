package com.aitrics.vital.api.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;

public record InferenceResponse(
    @JsonProperty("risk_score")
    Double riskScore,
    
    @JsonProperty("risk_level")
    String riskLevel,

    @JsonProperty("checked_rules")
    List<String> checkedRules,

    @JsonProperty("evaluated_at")
    LocalDate evaluatedAt


    ) {
    public static InferenceResponse of(LocalDate evaluatedAt, double riskScore, List<String> checkedRules) {
        String riskLevel = determineRiskLevel(riskScore);
        return new InferenceResponse(riskScore, riskLevel, checkedRules,evaluatedAt);
    }
    
    private static String determineRiskLevel(double score) {
        if (score <= 0.3) return "LOW";
        if (score <= 0.7) return "MEDIUM";
        return "HIGH";
    }
}