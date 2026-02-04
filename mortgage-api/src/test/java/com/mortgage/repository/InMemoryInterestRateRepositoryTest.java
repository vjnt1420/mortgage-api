package com.mortgage.repository;

import com.mortgage.config.MortgageRatesConfig;
import com.mortgage.model.InterestRate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryInterestRateRepositoryTest {

    private InMemoryInterestRateRepository repository;

    @BeforeEach
    void setUp() {
        MortgageRatesConfig config = new MortgageRatesConfig();
        config.setRates(List.of(
                createRate(1, "2.50"),
                createRate(5, "3.00"),
                createRate(10, "3.50"),
                createRate(15, "4.00"),
                createRate(20, "4.25"),
                createRate(25, "4.50"),
                createRate(30, "4.75")
        ));
        repository = new InMemoryInterestRateRepository(config);
        repository.init();
    }

    private MortgageRatesConfig.Rate createRate(int maturityPeriod, String interestRate) {
        MortgageRatesConfig.Rate rate = new MortgageRatesConfig.Rate();
        rate.setMaturityPeriod(maturityPeriod);
        rate.setInterestRate(new BigDecimal(interestRate));
        return rate;
    }

    @Test
    @DisplayName("Should initialize with predefined interest rates")
    void shouldInitializeWithPredefinedRates() {
        List<InterestRate> rates = repository.findAll();

        assertThat(rates).isNotEmpty();
        assertThat(rates).hasSizeGreaterThanOrEqualTo(5);
    }

    @Test
    @DisplayName("Should return rates sorted by maturity period")
    void shouldReturnRatesSortedByMaturityPeriod() {
        List<InterestRate> rates = repository.findAll();

        for (int i = 0; i < rates.size() - 1; i++) {
            assertThat(rates.get(i).maturityPeriod())
                    .isLessThan(rates.get(i + 1).maturityPeriod());
        }
    }

    @Test
    @DisplayName("Should find rate by existing maturity period")
    void shouldFindRateByExistingMaturityPeriod() {
        Optional<InterestRate> rate = repository.findByMaturityPeriod(30);

        assertThat(rate).isPresent();
        assertThat(rate.get().maturityPeriod()).isEqualTo(30);
        assertThat(rate.get().interestRate()).isNotNull();
        assertThat(rate.get().lastUpdate()).isNotNull();
    }

    @Test
    @DisplayName("Should return empty for non-existent maturity period")
    void shouldReturnEmptyForNonExistentMaturityPeriod() {
        Optional<InterestRate> rate = repository.findByMaturityPeriod(99);

        assertThat(rate).isEmpty();
    }

    @Test
    @DisplayName("Should contain expected maturity periods")
    void shouldContainExpectedMaturityPeriods() {
        assertThat(repository.findByMaturityPeriod(1)).isPresent();
        assertThat(repository.findByMaturityPeriod(5)).isPresent();
        assertThat(repository.findByMaturityPeriod(10)).isPresent();
        assertThat(repository.findByMaturityPeriod(15)).isPresent();
        assertThat(repository.findByMaturityPeriod(20)).isPresent();
        assertThat(repository.findByMaturityPeriod(25)).isPresent();
        assertThat(repository.findByMaturityPeriod(30)).isPresent();
    }

    @Test
    @DisplayName("All rates should have positive interest values")
    void allRatesShouldHavePositiveInterestValues() {
        List<InterestRate> rates = repository.findAll();

        rates.forEach(rate -> {
            assertThat(rate.interestRate()).isPositive();
        });
    }

    @Test
    @DisplayName("All rates should have lastUpdate timestamp")
    void allRatesShouldHaveLastUpdateTimestamp() {
        List<InterestRate> rates = repository.findAll();

        rates.forEach(rate -> {
            assertThat(rate.lastUpdate()).isNotNull();
        });
    }
}
