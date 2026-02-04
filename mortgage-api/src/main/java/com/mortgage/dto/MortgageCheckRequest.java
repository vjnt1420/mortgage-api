package com.mortgage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
@Schema(description = "Request payload for mortgage feasibility check")
public record MortgageCheckRequest(
        @NotNull(message = "Income is required")
        @DecimalMin(value = "0.01", message = "Income must be greater than 0")
        @Schema(description = "Annual income amount", example = "50000.00", requiredMode = Schema.RequiredMode.REQUIRED)
        BigDecimal income,

        @NotNull(message = "Maturity period is required")
        @Min(value = 1, message = "Maturity period must be at least 1 year")
        @Schema(description = "Loan maturity period in years", example = "30", requiredMode = Schema.RequiredMode.REQUIRED)
        Integer maturityPeriod,

        @NotNull(message = "Loan value is required")
        @DecimalMin(value = "0.01", message = "Loan value must be greater than 0")
        @Schema(description = "Requested loan amount", example = "150000.00", requiredMode = Schema.RequiredMode.REQUIRED)
        BigDecimal loanValue,

        @NotNull(message = "Home value is required")
        @DecimalMin(value = "0.01", message = "Home value must be greater than 0")
        @Schema(description = "Value of the home/property", example = "200000.00", requiredMode = Schema.RequiredMode.REQUIRED)
        BigDecimal homeValue
) {
}
