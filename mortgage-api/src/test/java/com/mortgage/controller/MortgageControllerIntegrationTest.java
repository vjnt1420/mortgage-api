package com.mortgage.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mortgage.dto.MortgageCheckRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class MortgageControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("Successful Mortgage Check Tests")
    class SuccessfulMortgageCheckTests {

        @Test
        @DisplayName("POST /api/mortgage-check should return feasible for valid request within limits")
        void shouldReturnFeasibleForValidRequest() throws Exception {
            MortgageCheckRequest request = MortgageCheckRequest.builder()
                    .income(new BigDecimal("50000"))
                    .loanValue(new BigDecimal("150000"))
                    .homeValue(new BigDecimal("200000"))
                    .maturityPeriod(30)
                    .build();

            mockMvc.perform(post("/api/mortgage-check")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.feasible", is(true)))
                    .andExpect(jsonPath("$.monthlyCosts", notNullValue()))
                    .andExpect(jsonPath("$.monthlyCosts", greaterThan(0.0)));
        }

        @Test
        @DisplayName("POST /api/mortgage-check should return not feasible when loan exceeds 4x income")
        void shouldReturnNotFeasibleWhenExceedsIncomeLimit() throws Exception {
            MortgageCheckRequest request = MortgageCheckRequest.builder()
                    .income(new BigDecimal("50000"))
                    .loanValue(new BigDecimal("250000")) // More than 4 * 50000
                    .homeValue(new BigDecimal("300000"))
                    .maturityPeriod(30)
                    .build();

            mockMvc.perform(post("/api/mortgage-check")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.feasible", is(false)))
                    .andExpect(jsonPath("$.monthlyCosts", notNullValue()));
        }

        @Test
        @DisplayName("POST /api/mortgage-check should return not feasible when loan exceeds home value")
        void shouldReturnNotFeasibleWhenExceedsHomeValue() throws Exception {
            MortgageCheckRequest request = MortgageCheckRequest.builder()
                    .income(new BigDecimal("100000"))
                    .loanValue(new BigDecimal("250000"))
                    .homeValue(new BigDecimal("200000")) // Less than loan value
                    .maturityPeriod(30)
                    .build();

            mockMvc.perform(post("/api/mortgage-check")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.feasible", is(false)));
        }

        @Test
        @DisplayName("POST /api/mortgage-check should work with different maturity periods")
        void shouldWorkWithDifferentMaturityPeriods() throws Exception {
            MortgageCheckRequest request = MortgageCheckRequest.builder()
                    .income(new BigDecimal("100000"))
                    .loanValue(new BigDecimal("200000"))
                    .homeValue(new BigDecimal("300000"))
                    .maturityPeriod(10)
                    .build();

            mockMvc.perform(post("/api/mortgage-check")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.feasible", is(true)))
                    .andExpect(jsonPath("$.monthlyCosts", notNullValue()));
        }
    }

    @Nested
    @DisplayName("Validation Error Tests")
    class ValidationErrorTests {

        @Test
        @DisplayName("POST /api/mortgage-check should return 400 when income is missing")
        void shouldReturn400WhenIncomeMissing() throws Exception {
            String request = """
                    {
                        "loanValue": 150000,
                        "homeValue": 200000,
                        "maturityPeriod": 30
                    }
                    """;

            mockMvc.perform(post("/api/mortgage-check")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(request))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.fieldErrors", hasSize(greaterThan(0))))
                    .andExpect(jsonPath("$.fieldErrors[*].field", hasItem("income")));
        }

        @Test
        @DisplayName("POST /api/mortgage-check should return 400 when income is negative")
        void shouldReturn400WhenIncomeNegative() throws Exception {
            MortgageCheckRequest request = MortgageCheckRequest.builder()
                    .income(new BigDecimal("-1000"))
                    .loanValue(new BigDecimal("150000"))
                    .homeValue(new BigDecimal("200000"))
                    .maturityPeriod(30)
                    .build();

            mockMvc.perform(post("/api/mortgage-check")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.fieldErrors[*].field", hasItem("income")));
        }

        @Test
        @DisplayName("POST /api/mortgage-check should return 400 when maturity period is 0")
        void shouldReturn400WhenMaturityPeriodZero() throws Exception {
            MortgageCheckRequest request = MortgageCheckRequest.builder()
                    .income(new BigDecimal("50000"))
                    .loanValue(new BigDecimal("150000"))
                    .homeValue(new BigDecimal("200000"))
                    .maturityPeriod(0)
                    .build();

            mockMvc.perform(post("/api/mortgage-check")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.fieldErrors[*].field", hasItem("maturityPeriod")));
        }

        @Test
        @DisplayName("POST /api/mortgage-check should return 400 for empty request body")
        void shouldReturn400ForEmptyRequestBody() throws Exception {
            mockMvc.perform(post("/api/mortgage-check")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.fieldErrors", hasSize(4)));
        }

        @Test
        @DisplayName("POST /api/mortgage-check should return 400 for invalid JSON")
        void shouldReturn400ForInvalidJson() throws Exception {
            mockMvc.perform(post("/api/mortgage-check")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("invalid json"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", containsString("Invalid")));
        }
    }

    @Nested
    @DisplayName("Interest Rate Not Found Tests")
    class InterestRateNotFoundTests {

        @Test
        @DisplayName("POST /api/mortgage-check should return 400 when maturity period not found")
        void shouldReturn400WhenMaturityPeriodNotFound() throws Exception {
            MortgageCheckRequest request = MortgageCheckRequest.builder()
                    .income(new BigDecimal("50000"))
                    .loanValue(new BigDecimal("150000"))
                    .homeValue(new BigDecimal("200000"))
                    .maturityPeriod(99) // Non-existent maturity period
                    .build();

            mockMvc.perform(post("/api/mortgage-check")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", containsString("Interest rate not found")));
        }
    }
}
