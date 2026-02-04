package com.mortgage.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class InterestRateControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Nested
    @DisplayName("Successful interest rate retrieval tests")
    class SuccessfulRetrievalTests {

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

    @Nested
    @DisplayName("Negative test cases")
    class NegativeTests {

        @Test
        @DisplayName("POST /api/interest-rates should return 405 Method Not Allowed")
        void shouldReturn405ForPostMethod() throws Exception {
            mockMvc.perform(post("/api/interest-rates")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isMethodNotAllowed());
        }

        @Test
        @DisplayName("PUT /api/interest-rates should return 405 Method Not Allowed")
        void shouldReturn405ForPutMethod() throws Exception {
            mockMvc.perform(put("/api/interest-rates")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isMethodNotAllowed());
        }

        @Test
        @DisplayName("DELETE /api/interest-rates should return 405 Method Not Allowed")
        void shouldReturn405ForDeleteMethod() throws Exception {
            mockMvc.perform(delete("/api/interest-rates"))
                    .andExpect(status().isMethodNotAllowed());
        }

        @Test
        @DisplayName("GET /api/interest-rates/invalid should return 404 Not Found")
        void shouldReturn404ForInvalidEndpoint() throws Exception {
            mockMvc.perform(get("/api/interest-rates/invalid")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("GET /api/interest-rates with unsupported Accept header should return 406 Not Acceptable")
        void shouldReturn406ForUnsupportedAcceptHeader() throws Exception {
            mockMvc.perform(get("/api/interest-rates")
                            .accept(MediaType.APPLICATION_XML))
                    .andExpect(status().isNotAcceptable());
        }
    }
}
