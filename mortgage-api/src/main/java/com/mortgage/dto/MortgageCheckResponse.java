package com.mortgage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response for mortgage feasibility check")
public class MortgageCheckResponse {

    @Schema(description = "Indicates if the mortgage is feasible based on business rules", example = "true")
    private boolean feasible;

    @Schema(description = "Monthly mortgage payment amount", example = "943.56")
    private BigDecimal monthlyCosts;
}
