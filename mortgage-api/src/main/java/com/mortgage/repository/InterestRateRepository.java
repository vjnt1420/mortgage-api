package com.mortgage.repository;

import com.mortgage.model.InterestRate;

import java.util.List;
import java.util.Optional;

public interface InterestRateRepository {

    List<InterestRate> findAll();

    Optional<InterestRate> findByMaturityPeriod(Integer maturityPeriod);
}
