# Mortgage API

A production-ready REST API for mortgage interest rates and feasibility checks built with Spring Boot.

## Features

- **GET /api/interest-rates** - Retrieve current mortgage interest rates for various maturity periods
- **POST /api/mortgage-check** - Check mortgage feasibility based on business rules and calculate monthly costs

## Business Rules

1. Mortgage amount should not exceed **4 times the annual income**
2. Mortgage amount should not exceed the **home value**

## Technology Stack

- Java 17
- Spring Boot 3.2.2
- Spring Web
- Spring Validation
- SpringDoc OpenAPI (Swagger)
- Lombok
- JUnit 5 & Mockito
- JaCoCo (Code Coverage)

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.8+

### Building the Application

```bash
cd mortgage-api
mvn clean package
```

### Running the Application

```bash
mvn spring-boot:run
```

Or run the JAR directly:

```bash
java -jar target/mortgage-api-1.0.0.jar
```

The application will start on `http://localhost:8080`

### Running Tests

```bash
mvn test
```

To generate a test coverage report:

```bash
mvn test jacoco:report
```

Coverage report will be available at `target/site/jacoco/index.html`

## API Documentation

Once the application is running, access the Swagger UI:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

## API Endpoints

### GET /api/interest-rates

Returns a list of current mortgage interest rates.

**Response Example:**
```json
[
  {
    "maturityPeriod": 10,
    "interestRate": 3.50,
    "lastUpdate": "2024-01-15T10:30:00Z"
  },
  {
    "maturityPeriod": 30,
    "interestRate": 4.75,
    "lastUpdate": "2024-01-15T10:30:00Z"
  }
]
```

### POST /api/mortgage-check

Checks if a mortgage is feasible and calculates monthly costs.

**Request Body:**
```json
{
  "income": 50000.00,
  "maturityPeriod": 30,
  "loanValue": 150000.00,
  "homeValue": 200000.00
}
```

**Response Example (Feasible):**
```json
{
  "feasible": true,
  "monthlyCosts": 782.47
}
```

**Response Example (Not Feasible):**
```json
{
  "feasible": false,
  "monthlyCosts": 1304.12
}
```

## Available Interest Rate Periods

The system initializes with the following maturity periods (in years):
- 1, 5, 10, 15, 20, 25, 30

## Health & Monitoring

Spring Actuator endpoints are available for production monitoring:

- **Health**: http://localhost:8080/actuator/health
- **Info**: http://localhost:8080/actuator/info
- **Metrics**: http://localhost:8080/actuator/metrics

## Project Structure

```
mortgage-api/
├── src/
│   ├── main/
│   │   ├── java/com/mortgage/
│   │   │   ├── config/          # Configuration classes
│   │   │   ├── controller/      # REST controllers
│   │   │   ├── dto/             # Data transfer objects
│   │   │   ├── exception/       # Exception handling
│   │   │   ├── model/           # Domain models
│   │   │   ├── repository/      # Data repositories
│   │   │   ├── service/         # Business logic
│   │   │   └── MortgageApiApplication.java
│   │   └── resources/
│   │       └── application.yml
│   └── test/
│       └── java/com/mortgage/   # Unit & Integration tests
├── pom.xml
└── README.md
```

## Error Handling

The API returns structured error responses:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/mortgage-check",
  "timestamp": "2024-01-15T10:30:00Z",
  "fieldErrors": [
    {
      "field": "income",
      "message": "Income is required",
      "rejectedValue": null
    }
  ]
}
```
