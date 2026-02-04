package com.mortgage.repository;

import com.mortgage.config.MortgageRatesConfig;
import com.mortgage.model.InterestRate;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Repository
@RequiredArgsConstructor
public class InMemoryInterestRateRepository implements InterestRateRepository {

    private final MortgageRatesConfig mortgageRatesConfig;
    private final ConcurrentHashMap<Integer, InterestRate> interestRates = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        log.info("Initializing interest rates from configuration");

        Instant now = Instant.now();

        // Load interest rates from configuration
        mortgageRatesConfig.getRates().forEach(rate ->
            interestRates.put(rate.getMaturityPeriod(), InterestRate.builder()
                    .maturityPeriod(rate.getMaturityPeriod())
                    .interestRate(rate.getInterestRate())
                    .lastUpdate(now)
                    .build())
        );

        log.info("Initialized {} interest rates from configuration", interestRates.size());
    }

    @Override
    public List<InterestRate> findAll() {
        List<InterestRate> rates = new ArrayList<>(interestRates.values());
        rates.sort((r1, r2) -> r1.maturityPeriod().compareTo(r2.maturityPeriod()));
        return Collections.unmodifiableList(rates);
    }

    @Override
    public Optional<InterestRate> findByMaturityPeriod(Integer maturityPeriod) {
        return Optional.ofNullable(interestRates.get(maturityPeriod));
    }
}
