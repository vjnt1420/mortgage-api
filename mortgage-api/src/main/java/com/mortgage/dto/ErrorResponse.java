package com.mortgage.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Error response payload")
public class ErrorResponse {

    @Schema(description = "HTTP status code", example = "400")
    private int status;

    @Schema(description = "Error type", example = "Bad Request")
    private String error;

    @Schema(description = "Error message", example = "Validation failed")
    private String message;

    @Schema(description = "Request path", example = "/api/mortgage-check")
    private String path;

    @Schema(description = "Timestamp of the error")
    private Instant timestamp;

    @Schema(description = "List of validation errors")
    private List<FieldError> fieldErrors;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Field-level validation error")
    public static class FieldError {
        @Schema(description = "Field name", example = "income")
        private String field;

        @Schema(description = "Error message", example = "Income is required")
        private String message;

        @Schema(description = "Rejected value")
        private Object rejectedValue;
    }
}
