package com.aitrics.vital.api.error;

public enum ErrorCode {
    NOT_FOUND("RESOURCE_NOT_FOUND", "The requested resource was not found"),
    CONFLICT("RESOURCE_CONFLICT", "Resource conflict occurred"),
    VALIDATION_ERROR("VALIDATION_FAILED", "Validation failed"),
    UNAUTHORIZED("UNAUTHORIZED_ACCESS", "Unauthorized access"),
    INTERNAL_ERROR("INTERNAL_SERVER_ERROR", "Internal server error occurred");
    
    private final String code;
    private final String message;
    
    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public String getCode() { return code; }
    public String getMessage() { return message; }
}