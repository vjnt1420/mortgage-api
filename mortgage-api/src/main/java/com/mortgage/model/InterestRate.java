package com.mortgage.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Mortgage interest rate information")
public class InterestRate {

    @Schema(description = "Maturity period in years", example = "10")
    private Integer maturityPeriod;

    @Schema(description = "Annual interest rate as percentage", example = "3.5")
    private BigDecimal interestRate;

    @Schema(description = "Timestamp of last update")
    private Instant lastUpdate;
}
