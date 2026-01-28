package com.mortgage.service;

import com.mortgage.dto.MortgageCheckRequest;
import com.mortgage.dto.MortgageCheckResponse;
import com.mortgage.exception.InterestRateNotFoundException;
import com.mortgage.model.InterestRate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MortgageServiceTest {

    @Mock
    private InterestRateService interestRateService;

    @InjectMocks
    private MortgageServiceImpl mortgageService;

    private InterestRate testRate;

    @BeforeEach
    void setUp() {
        testRate = InterestRate.builder()
                .maturityPeriod(30)
                .interestRate(new BigDecimal("4.75"))
                .lastUpdate(Instant.now())
                .build();
    }

    @Nested
    @DisplayName("Feasibility Tests")
    class FeasibilityTests {

        @Test
        @DisplayName("Should return feasible when loan is within income and home value limits")
        void shouldReturnFeasibleWhenWithinLimits() {
            // Given
            MortgageCheckRequest request = MortgageCheckRequest.builder()
                    .income(new BigDecimal("50000"))
                    .loanValue(new BigDecimal("150000"))
                    .homeValue(new BigDecimal("200000"))
                    .maturityPeriod(30)
                    .build();

            when(interestRateService.getInterestRateByMaturityPeriod(30)).thenReturn(testRate);

            // When
            MortgageCheckResponse response = mortgageService.checkMortgageFeasibility(request);

            // Then
            assertThat(response.isFeasible()).isTrue();
            assertThat(response.getMonthlyCosts()).isNotNull();
        }

        @Test
        @DisplayName("Should return not feasible when loan exceeds 4x income")
        void shouldReturnNotFeasibleWhenExceedsIncomeLimit() {
            // Given
            MortgageCheckRequest request = MortgageCheckRequest.builder()
                    .income(new BigDecimal("50000"))
                    .loanValue(new BigDecimal("250000")) // More than 4 * 50000 = 200000
                    .homeValue(new BigDecimal("300000"))
                    .maturityPeriod(30)
                    .build();

            when(interestRateService.getInterestRateByMaturityPeriod(30)).thenReturn(testRate);

            // When
            MortgageCheckResponse response = mortgageService.checkMortgageFeasibility(request);

            // Then
            assertThat(response.isFeasible()).isFalse();
        }

        @Test
        @DisplayName("Should return not feasible when loan exceeds home value")
        void shouldReturnNotFeasibleWhenExceedsHomeValue() {
            // Given
            MortgageCheckRequest request = MortgageCheckRequest.builder()
                    .income(new BigDecimal("100000"))
                    .loanValue(new BigDecimal("250000"))
                    .homeValue(new BigDecimal("200000")) // Less than loan value
                    .maturityPeriod(30)
                    .build();

            when(interestRateService.getInterestRateByMaturityPeriod(30)).thenReturn(testRate);

            // When
            MortgageCheckResponse response = mortgageService.checkMortgageFeasibility(request);

            // Then
            assertThat(response.isFeasible()).isFalse();
        }

        @Test
        @DisplayName("Should return feasible when loan equals exactly 4x income")
        void shouldReturnFeasibleWhenEqualsIncomeLimit() {
            // Given
            MortgageCheckRequest request = MortgageCheckRequest.builder()
                    .income(new BigDecimal("50000"))
                    .loanValue(new BigDecimal("200000")) // Exactly 4 * 50000
                    .homeValue(new BigDecimal("250000"))
                    .maturityPeriod(30)
                    .build();

            when(interestRateService.getInterestRateByMaturityPeriod(30)).thenReturn(testRate);

            // When
            MortgageCheckResponse response = mortgageService.checkMortgageFeasibility(request);

            // Then
            assertThat(response.isFeasible()).isTrue();
        }

        @Test
        @DisplayName("Should return feasible when loan equals home value")
        void shouldReturnFeasibleWhenEqualsHomeValue() {
            // Given
            MortgageCheckRequest request = MortgageCheckRequest.builder()
                    .income(new BigDecimal("100000"))
                    .loanValue(new BigDecimal("200000"))
                    .homeValue(new BigDecimal("200000")) // Equals loan value
                    .maturityPeriod(30)
                    .build();

            when(interestRateService.getInterestRateByMaturityPeriod(30)).thenReturn(testRate);

            // When
            MortgageCheckResponse response = mortgageService.checkMortgageFeasibility(request);

            // Then
            assertThat(response.isFeasible()).isTrue();
        }
    }

    @Nested
    @DisplayName("Monthly Cost Calculation Tests")
    class MonthlyCostTests {

        @Test
        @DisplayName("Should calculate correct monthly costs for standard mortgage")
        void shouldCalculateCorrectMonthlyCosts() {
            // Given
            MortgageCheckRequest request = MortgageCheckRequest.builder()
                    .income(new BigDecimal("100000"))
                    .loanValue(new BigDecimal("200000"))
                    .homeValue(new BigDecimal("250000"))
                    .maturityPeriod(30)
                    .build();

            when(interestRateService.getInterestRateByMaturityPeriod(30)).thenReturn(testRate);

            // When
            MortgageCheckResponse response = mortgageService.checkMortgageFeasibility(request);

            // Then
            // Expected monthly payment for $200,000 loan at 4.75% for 30 years
            // Using standard mortgage formula: ~$1043.29
            assertThat(response.getMonthlyCosts())
                    .isGreaterThan(new BigDecimal("1000"))
                    .isLessThan(new BigDecimal("1100"));
        }

        @Test
        @DisplayName("Should handle different maturity periods")
        void shouldHandleDifferentMaturityPeriods() {
            // Given
            InterestRate rate10Year = InterestRate.builder()
                    .maturityPeriod(10)
                    .interestRate(new BigDecimal("3.50"))
                    .lastUpdate(Instant.now())
                    .build();

            MortgageCheckRequest request = MortgageCheckRequest.builder()
                    .income(new BigDecimal("100000"))
                    .loanValue(new BigDecimal("100000"))
                    .homeValue(new BigDecimal("150000"))
                    .maturityPeriod(10)
                    .build();

            when(interestRateService.getInterestRateByMaturityPeriod(10)).thenReturn(rate10Year);

            // When
            MortgageCheckResponse response = mortgageService.checkMortgageFeasibility(request);

            // Then
            // Shorter term = higher monthly payment
            assertThat(response.getMonthlyCosts()).isGreaterThan(new BigDecimal("900"));
        }

        @Test
        @DisplayName("Should throw exception when maturity period not found")
        void shouldThrowExceptionWhenMaturityPeriodNotFound() {
            // Given
            MortgageCheckRequest request = MortgageCheckRequest.builder()
                    .income(new BigDecimal("50000"))
                    .loanValue(new BigDecimal("150000"))
                    .homeValue(new BigDecimal("200000"))
                    .maturityPeriod(99) // Non-existent maturity period
                    .build();

            when(interestRateService.getInterestRateByMaturityPeriod(99))
                    .thenThrow(new InterestRateNotFoundException(99));

            // When/Then
            assertThatThrownBy(() -> mortgageService.checkMortgageFeasibility(request))
                    .isInstanceOf(InterestRateNotFoundException.class)
                    .hasMessageContaining("99");
        }
    }
}
