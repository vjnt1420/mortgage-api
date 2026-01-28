package com.mortgage.controller;

import com.mortgage.dto.ErrorResponse;
import com.mortgage.model.InterestRate;
import com.mortgage.service.InterestRateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Interest Rates", description = "Mortgage interest rate operations")
public class InterestRateController {

    private final InterestRateService interestRateService;

    @GetMapping(value = "/interest-rates", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Get all interest rates",
            description = "Retrieves the list of current mortgage interest rates for all available maturity periods"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved interest rates",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = InterestRate.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<List<InterestRate>> getInterestRates() {
        log.info("GET /api/interest-rates - Fetching all interest rates");
        List<InterestRate> rates = interestRateService.getAllInterestRates();
        log.info("Returning {} interest rates", rates.size());
        return ResponseEntity.ok(rates);
    }
}
