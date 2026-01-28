package com.mortgage.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class InterestRateControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /api/interest-rates should return list of interest rates")
    void shouldReturnListOfInterestRates() throws Exception {
        mockMvc.perform(get("/api/interest-rates")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$[0].maturityPeriod", notNullValue()))
                .andExpect(jsonPath("$[0].interestRate", notNullValue()))
                .andExpect(jsonPath("$[0].lastUpdate", notNullValue()));
    }

    @Test
    @DisplayName("GET /api/interest-rates should return rates sorted by maturity period")
    void shouldReturnRatesSortedByMaturityPeriod() throws Exception {
        mockMvc.perform(get("/api/interest-rates")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].maturityPeriod").value(1))
                .andExpect(jsonPath("$[1].maturityPeriod").value(5))
                .andExpect(jsonPath("$[2].maturityPeriod").value(10));
    }

    @Test
    @DisplayName("GET /api/interest-rates should contain expected maturity periods")
    void shouldContainExpectedMaturityPeriods() throws Exception {
        mockMvc.perform(get("/api/interest-rates")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].maturityPeriod", hasItems(10, 20, 30)));
    }
}
