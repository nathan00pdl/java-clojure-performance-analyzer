# Performance Analyzer between programming paradigms

## Overview
This project implements a comparative performance study between **Object-Oriented Programming (OOP)** and **Functional Programming (FP)** through a REST API that calculates compound interest. The application provides two implementations of the same business rule:
- **Java Implementation**: Imperative/object-oriented paradigm
- **Clojure Implementation**: Functional paradigm


## Objective
To evaluate and compare performance, scalability and behavior under different loads between the two programming paradigms, in high concurrency scenarios, using automated load tests with Gatling.


## Architecture
### Layered Structure (MVC)
```
src/main/java/com/example/financial_calculator_fp/
├── config/                         
│   ├── ClojureConfiguration.java
│   ├── ClojureNamespaceLoader.java
│   └── SwaggerConfig.java
├── controllers/  
│   ├── ClojureCompoundInterestController.java
│   └── JavaCompoundInterestController.java
├── services/                       
│   ├── CompoundInterestService.java
│   └── JavaCompoundInterestService.java
├── models/                         
│   ├── request/
│   └── response/
└── exceptions/                     

src/main/clojure/com/example/financial_calculator_fp/
└── service/
    └── compound_interest_service.clj  # Functional Implementation

src/test/javaGatling/com/example/financial_calculator_fp/
└── performance/
    └── CompoundInterestSimulation.java  # Gatling Tests
```


## Technologies Used
### Core Framework
- **Spring Boot 3.4.5**: Main framework
- **Spring Web**: REST APIs
- **Spring Validation**: Data validation

### Languages
- **Java 17**: Imperative implementation
- **Clojure**: Functional implementation

### Testing and Performance
- **Gatling**: Load testing and performance

### Documentation
- **Swagger/OpenAPI 3**: Automatic API documentation

### Build and Dependencies
- **Maven**: Dependency management and build


## Prerequisites
- **Java 17** or higher
- **Maven 3.8+**
- **Git**


## Installation and Execution
### 1. Repository Clone
```bash
git clone https://github.com/your-username/paradigm-performance-analyzer.git
cd paradigm-performance-analyzer
```

### 2. Dependencies Installation
```bash
mvn clean install
```

### 3. Application Execution
```bash
mvn spring-boot:run
```

The application will be available at: `http://localhost:8080`

### 4. Health Check
```bash
# Check if Clojure service is working
curl http://localhost:8080/api/compound-interest-clojure/health
```


## API Documentation
### Swagger UI
Access: `http://localhost:8080/swagger-ui.html`

### Available Endpoints

#### Java Implementation (OOP)
```http
POST /api/compound-interest-java/calculate
Content-Type: application/json

{
  "initialAmount": 1000.0,
  "annualInterestRate": 5.0,
  "years": 10
}
```

#### Clojure Implementation (FP)
```http
POST /api/compound-interest-clojure/calculate
Content-Type: application/json

{
  "initialAmount": 1000.0,
  "annualInterestRate": 5.0,
  "years": 10
}
```

### Response Example
```json
{
  "summaryResults": {
    "initialInvestment": 1000.0,
    "finalBalance": 1628.895,
    "totalInterestEarned": 628.895,
    "totalAdditionalContributions": 0.0
  },
  "yearlyDetailsList": [
    {
      "year": 1,
      "startBalance": 1000.0,
      "endBalance": 1050.0,
      "interestEarned": 50.0,
      "additionalContribution": 0.0
    }
    // ... more years
  ]
}
```


## Performance Testing
### Running Gatling Tests
```bash
# Run all tests
mvn gatling:test
```

### Performance Reports
Reports are generated in: `target/gatling/`


## Metrics and Monitoring
### Collected Metrics
- **Throughput**:
    - Total requests
    - OK requests
    - KO requests
    - Error rate: % KO
- **Response Time (ms)**:
    - Min, Mean and Max 
    - Consistency: standard deviation
    - Percentiles: 50th, 75th, 95th and 99th


## Validations and Restrictions
### Input Validations
- `initialAmount`: >= 0
- `annualInterestRate`: > 0
- `years`: > 0 and <= 200

### Error Handling
- Input validation with specific messages
- Uncaught exception handling
- Detailed logs for debugging


## Paradigm Comparison
### Java Implementation (Imperative)
- **Structure**: Traditional loops, mutable lists
- **Advantages**: Familiar, direct debugging
- **Focus**: Raw performance, memory usage

### Clojure Implementation (Functional)
- **Structure**: Tail recursion, immutable structures
- **Advantages**: Thread-safety, composability
- **Focus**: Concurrency, scalability


## Useful Commands
### Development
```bash
# Complete cleanup
mvn clean

# Compilation
mvn compile

# Run unit tests
mvn test

# Check dependencies
mvn dependency:tree

# Code analysis
mvn spotbugs:check
```
