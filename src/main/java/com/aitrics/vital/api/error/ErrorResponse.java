package com.aitrics.vital.api.error;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponse(
    String code,
    String message,
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime timestamp,
    
    String path,
    List<String> details
) {
    public static ErrorResponse of(ErrorCode errorCode, String path, String message) {
        return new ErrorResponse(
            errorCode.getCode(),
            message != null ? message : errorCode.getMessage(),
            LocalDateTime.now(),
            path,
            null
        );
    }
    
    public static ErrorResponse of(ErrorCode errorCode, String path, String message, List<String> details) {
        return new ErrorResponse(
            errorCode.getCode(),
            message != null ? message : errorCode.getMessage(),
            LocalDateTime.now(),
            path,
            details
        );
    }
}