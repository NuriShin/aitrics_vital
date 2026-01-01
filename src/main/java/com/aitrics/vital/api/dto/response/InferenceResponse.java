package com.aitrics.vital.api.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record InferenceResponse(
    @JsonProperty("risk_score")
    Double riskScore,
    
    @JsonProperty("risk_level")
    String riskLevel
) {
    public static InferenceResponse of(double riskScore) {
        String riskLevel = determineRiskLevel(riskScore);
        return new InferenceResponse(riskScore, riskLevel);
    }
    
    private static String determineRiskLevel(double score) {
        if (score <= 0.3) return "LOW";
        if (score <= 0.7) return "MEDIUM";
        return "HIGH";
    }
}