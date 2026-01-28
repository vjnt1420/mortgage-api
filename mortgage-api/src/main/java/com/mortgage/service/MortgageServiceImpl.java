package com.mortgage.service;

import com.mortgage.dto.MortgageCheckRequest;
import com.mortgage.dto.MortgageCheckResponse;
import com.mortgage.model.InterestRate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

@Slf4j
@Service
@RequiredArgsConstructor
public class MortgageServiceImpl implements MortgageService {

    private static final BigDecimal MAX_INCOME_MULTIPLIER = new BigDecimal("4");
    private static final int MONTHS_PER_YEAR = 12;
    private static final int CALCULATION_SCALE = 10;
    private static final int RESULT_SCALE = 2;
    private static final MathContext MATH_CONTEXT = new MathContext(CALCULATION_SCALE, RoundingMode.HALF_UP);

    private final InterestRateService interestRateService;

    @Override
    public MortgageCheckResponse checkMortgageFeasibility(MortgageCheckRequest request) {
        log.info("Processing mortgage check request: income={}, loanValue={}, homeValue={}, maturityPeriod={}",
                request.getIncome(), request.getLoanValue(), request.getHomeValue(), request.getMaturityPeriod());

        // Check business rules for feasibility
        boolean feasible = isMortgageFeasible(request);

        // Calculate monthly costs
        BigDecimal monthlyCosts = calculateMonthlyCosts(request);

        log.info("Mortgage check result: feasible={}, monthlyCosts={}", feasible, monthlyCosts);

        return MortgageCheckResponse.builder()
                .feasible(feasible)
                .monthlyCosts(monthlyCosts)
                .build();
    }

    private boolean isMortgageFeasible(MortgageCheckRequest request) {
        BigDecimal maxAllowedByIncome = request.getIncome().multiply(MAX_INCOME_MULTIPLIER);

        // Rule 1: Mortgage should not exceed 4 times the income
        boolean withinIncomeLimit = request.getLoanValue().compareTo(maxAllowedByIncome) <= 0;
        if (!withinIncomeLimit) {
            log.debug("Loan value {} exceeds maximum allowed by income: {}",
                    request.getLoanValue(), maxAllowedByIncome);
        }

        // Rule 2: Mortgage should not exceed the home value
        boolean withinHomeValueLimit = request.getLoanValue().compareTo(request.getHomeValue()) <= 0;
        if (!withinHomeValueLimit) {
            log.debug("Loan value {} exceeds home value: {}",
                    request.getLoanValue(), request.getHomeValue());
        }

        return withinIncomeLimit && withinHomeValueLimit;
    }

    private BigDecimal calculateMonthlyCosts(MortgageCheckRequest request) {
        // Get the interest rate for the requested maturity period
        InterestRate interestRate = interestRateService.getInterestRateByMaturityPeriod(request.getMaturityPeriod());

        // Convert annual interest rate percentage to monthly decimal rate
        // e.g., 4.5% -> 0.045 / 12 = 0.00375
        BigDecimal annualRate = interestRate.getInterestRate()
                .divide(BigDecimal.valueOf(100), CALCULATION_SCALE, RoundingMode.HALF_UP);
        BigDecimal monthlyRate = annualRate
                .divide(BigDecimal.valueOf(MONTHS_PER_YEAR), CALCULATION_SCALE, RoundingMode.HALF_UP);

        // Calculate total number of payments
        int totalPayments = request.getMaturityPeriod() * MONTHS_PER_YEAR;

        // Calculate monthly payment using the standard mortgage formula:
        // M = P * [r(1+r)^n] / [(1+r)^n - 1]
        // Where:
        // M = monthly payment
        // P = principal (loan value)
        // r = monthly interest rate
        // n = total number of payments

        if (monthlyRate.compareTo(BigDecimal.ZERO) == 0) {
            // If interest rate is 0, simply divide principal by number of payments
            return request.getLoanValue()
                    .divide(BigDecimal.valueOf(totalPayments), RESULT_SCALE, RoundingMode.HALF_UP);
        }

        // (1 + r)^n
        BigDecimal onePlusRate = BigDecimal.ONE.add(monthlyRate);
        BigDecimal compoundFactor = onePlusRate.pow(totalPayments, MATH_CONTEXT);

        // r * (1 + r)^n
        BigDecimal numerator = monthlyRate.multiply(compoundFactor, MATH_CONTEXT);

        // (1 + r)^n - 1
        BigDecimal denominator = compoundFactor.subtract(BigDecimal.ONE);

        // P * [r(1+r)^n] / [(1+r)^n - 1]
        BigDecimal monthlyPayment = request.getLoanValue()
                .multiply(numerator, MATH_CONTEXT)
                .divide(denominator, RESULT_SCALE, RoundingMode.HALF_UP);

        return monthlyPayment;
    }
}
