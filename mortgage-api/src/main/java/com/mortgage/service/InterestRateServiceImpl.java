package com.mortgage.service;

import com.mortgage.exception.InterestRateNotFoundException;
import com.mortgage.model.InterestRate;
import com.mortgage.repository.InterestRateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InterestRateServiceImpl implements InterestRateService {

    private final InterestRateRepository interestRateRepository;

    @Override
    public List<InterestRate> getAllInterestRates() {
        log.debug("Fetching all interest rates");
        List<InterestRate> rates = interestRateRepository.findAll();
        log.debug("Found {} interest rates", rates.size());
        return rates;
    }

    @Override
    public InterestRate getInterestRateByMaturityPeriod(Integer maturityPeriod) {
        log.debug("Fetching interest rate for maturity period: {} years", maturityPeriod);
        return interestRateRepository.findByMaturityPeriod(maturityPeriod)
                .orElseThrow(() -> new InterestRateNotFoundException(maturityPeriod));
    }
}
