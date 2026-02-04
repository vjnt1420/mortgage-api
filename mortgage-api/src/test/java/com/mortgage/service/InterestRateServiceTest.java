package com.mortgage.service;

import com.mortgage.exception.InterestRateNotFoundException;
import com.mortgage.model.InterestRate;
import com.mortgage.repository.InterestRateRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InterestRateServiceTest {

    @Mock
    private InterestRateRepository interestRateRepository;

    @InjectMocks
    private InterestRateServiceImpl interestRateService;

    @Test
    @DisplayName("Should return all interest rates")
    void shouldReturnAllInterestRates() {
        // Given
        Instant now = Instant.now();
        List<InterestRate> rates = List.of(
                InterestRate.builder().maturityPeriod(10).interestRate(new BigDecimal("3.5")).lastUpdate(now).build(),
                InterestRate.builder().maturityPeriod(20).interestRate(new BigDecimal("4.0")).lastUpdate(now).build(),
                InterestRate.builder().maturityPeriod(30).interestRate(new BigDecimal("4.5")).lastUpdate(now).build()
        );
        when(interestRateRepository.findAll()).thenReturn(rates);

        // When
        List<InterestRate> result = interestRateService.getAllInterestRates();

        // Then
        assertThat(result).hasSize(3);
        assertThat(result).extracting(InterestRate::maturityPeriod)
                .containsExactly(10, 20, 30);
    }

    @Test
    @DisplayName("Should return empty list when no rates exist")
    void shouldReturnEmptyListWhenNoRatesExist() {
        // Given
        when(interestRateRepository.findAll()).thenReturn(List.of());

        // When
        List<InterestRate> result = interestRateService.getAllInterestRates();

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should return interest rate for valid maturity period")
    void shouldReturnInterestRateForValidMaturityPeriod() {
        // Given
        InterestRate rate = InterestRate.builder()
                .maturityPeriod(30)
                .interestRate(new BigDecimal("4.75"))
                .lastUpdate(Instant.now())
                .build();
        when(interestRateRepository.findByMaturityPeriod(30)).thenReturn(Optional.of(rate));

        // When
        InterestRate result = interestRateService.getInterestRateByMaturityPeriod(30);

        // Then
        assertThat(result.maturityPeriod()).isEqualTo(30);
        assertThat(result.interestRate()).isEqualByComparingTo(new BigDecimal("4.75"));
    }

    @Test
    @DisplayName("Should throw exception when maturity period not found")
    void shouldThrowExceptionWhenMaturityPeriodNotFound() {
        // Given
        when(interestRateRepository.findByMaturityPeriod(99)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> interestRateService.getInterestRateByMaturityPeriod(99))
                .isInstanceOf(InterestRateNotFoundException.class)
                .hasMessageContaining("99");
    }
}
