package com.mortgage.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "mortgage")
public class MortgageRatesConfig {

    private List<Rate> rates = new ArrayList<>();

    @Data
    public static class Rate {
        private Integer maturityPeriod;
        private BigDecimal interestRate;
    }
}
