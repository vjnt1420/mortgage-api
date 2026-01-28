package com.mortgage.exception;

public class InterestRateNotFoundException extends RuntimeException {

    public InterestRateNotFoundException(Integer maturityPeriod) {
        super("Interest rate not found for maturity period: " + maturityPeriod + " years");
    }
}
