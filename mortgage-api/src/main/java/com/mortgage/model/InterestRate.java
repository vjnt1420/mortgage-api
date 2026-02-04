package com.mortgage.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;

@Builder
@Schema(description = "Mortgage interest rate information")
public record InterestRate(
        @Schema(description = "Maturity period in years", example = "10")
        Integer maturityPeriod,

        @Schema(description = "Annual interest rate as percentage", example = "3.5")
        BigDecimal interestRate,

        @Schema(description = "Timestamp of last update")
        Instant lastUpdate
) {
}
