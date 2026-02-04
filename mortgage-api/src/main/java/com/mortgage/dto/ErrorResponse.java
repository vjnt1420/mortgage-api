package com.mortgage.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Error response payload")
public record ErrorResponse(
        @Schema(description = "HTTP status code", example = "400")
        int status,

        @Schema(description = "Error type", example = "Bad Request")
        String error,

        @Schema(description = "Error message", example = "Validation failed")
        String message,

        @Schema(description = "Request path", example = "/api/mortgage-check")
        String path,

        @Schema(description = "Timestamp of the error")
        Instant timestamp,

        @Schema(description = "List of validation errors")
        List<FieldError> fieldErrors
) {
    @Builder
    @Schema(description = "Field-level validation error")
    public record FieldError(
            @Schema(description = "Field name", example = "income")
            String field,

            @Schema(description = "Error message", example = "Income is required")
            String message,

            @Schema(description = "Rejected value")
            Object rejectedValue
    ) {
    }
}
