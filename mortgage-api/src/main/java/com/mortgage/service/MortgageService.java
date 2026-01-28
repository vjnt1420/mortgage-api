package com.mortgage.service;

import com.mortgage.dto.MortgageCheckRequest;
import com.mortgage.dto.MortgageCheckResponse;

public interface MortgageService {

    MortgageCheckResponse checkMortgageFeasibility(MortgageCheckRequest request);
}
