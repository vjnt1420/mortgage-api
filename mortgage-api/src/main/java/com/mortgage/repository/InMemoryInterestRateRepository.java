package com.mortgage.repository;

import com.mortgage.model.InterestRate;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Repository
public class InMemoryInterestRateRepository implements InterestRateRepository {

    private final ConcurrentHashMap<Integer, InterestRate> interestRates = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        log.info("Initializing interest rates in memory");

        Instant now = Instant.now();

        // Initialize with sample mortgage interest rates for different maturity periods
        addRate(1, new BigDecimal("2.50"), now);
        addRate(5, new BigDecimal("3.00"), now);
        addRate(10, new BigDecimal("3.50"), now);
        addRate(15, new BigDecimal("4.00"), now);
        addRate(20, new BigDecimal("4.25"), now);
        addRate(25, new BigDecimal("4.50"), now);
        addRate(30, new BigDecimal("4.75"), now);

        log.info("Initialized {} interest rates", interestRates.size());
    }

    private void addRate(int maturityPeriod, BigDecimal rate, Instant timestamp) {
        interestRates.put(maturityPeriod, InterestRate.builder()
                .maturityPeriod(maturityPeriod)
                .interestRate(rate)
                .lastUpdate(timestamp)
                .build());
    }

    @Override
    public List<InterestRate> findAll() {
        List<InterestRate> rates = new ArrayList<>(interestRates.values());
        rates.sort((r1, r2) -> r1.getMaturityPeriod().compareTo(r2.getMaturityPeriod()));
        return Collections.unmodifiableList(rates);
    }

    @Override
    public Optional<InterestRate> findByMaturityPeriod(Integer maturityPeriod) {
        return Optional.ofNullable(interestRates.get(maturityPeriod));
    }
}
