package com.mortgage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
@Schema(description = "Response for mortgage feasibility check")
public record MortgageCheckResponse(
        @Schema(description = "Indicates if the mortgage is feasible based on business rules", example = "true")
        boolean feasible,

        @Schema(description = "Monthly mortgage payment amount", example = "943.56")
        BigDecimal monthlyCosts
) {
}
