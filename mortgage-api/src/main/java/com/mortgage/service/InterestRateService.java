package com.mortgage.service;

import com.mortgage.model.InterestRate;

import java.util.List;

public interface InterestRateService {

    List<InterestRate> getAllInterestRates();

    InterestRate getInterestRateByMaturityPeriod(Integer maturityPeriod);
}
