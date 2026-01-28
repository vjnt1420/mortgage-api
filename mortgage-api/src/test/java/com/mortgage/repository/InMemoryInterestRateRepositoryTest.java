package com.mortgage.repository;

import com.mortgage.model.InterestRate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryInterestRateRepositoryTest {

    private InMemoryInterestRateRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryInterestRateRepository();
        repository.init();
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
            assertThat(rates.get(i).getMaturityPeriod())
                    .isLessThan(rates.get(i + 1).getMaturityPeriod());
        }
    }

    @Test
    @DisplayName("Should find rate by existing maturity period")
    void shouldFindRateByExistingMaturityPeriod() {
        Optional<InterestRate> rate = repository.findByMaturityPeriod(30);

        assertThat(rate).isPresent();
        assertThat(rate.get().getMaturityPeriod()).isEqualTo(30);
        assertThat(rate.get().getInterestRate()).isNotNull();
        assertThat(rate.get().getLastUpdate()).isNotNull();
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
            assertThat(rate.getInterestRate()).isPositive();
        });
    }

    @Test
    @DisplayName("All rates should have lastUpdate timestamp")
    void allRatesShouldHaveLastUpdateTimestamp() {
        List<InterestRate> rates = repository.findAll();

        rates.forEach(rate -> {
            assertThat(rate.getLastUpdate()).isNotNull();
        });
    }
}
