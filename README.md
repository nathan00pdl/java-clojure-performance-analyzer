# Performance Analyzer: Programming Paradigms Comparison

## Overview
Comparative performance study between programming paradigms through a REST API that calculates compound interest. The project implements three distinct approaches:

- **Java (OOP)**: Pure object-oriented implementation using imperative style
- **Clojure Idiomatic (FP)**: Pure functional implementation with immutable data structures
- **Clojure Interop Java (Hybrid OOP+FP)**: Strategic Java interoperability for performance optimization

## Research Objective
Evaluate whether strategic interoperability between Java and Clojure can achieve superior performance compared to using either paradigm in isolation, analyzing CPU usage, memory consumption, garbage collection overhead, and response time consistency under high-concurrency scenarios.

## Architecture

### Project Structure
```
src/
├── main/
│   ├── java/.../
│   │   ├── config/
│   │   │   ├── ClojureConfiguration.java      # Clojure-Spring integration
│   │   │   └── SwaggerConfig.java
│   │   ├── controllers/
│   │   │   ├── JavaCompoundInterestController.java
│   │   │   └── ClojureCompoundInterestController.java
│   │   ├── services/
│   │   │   ├── CompoundInterestService.java   # Interface
│   │   │   └── JavaCompoundInterestService.java
│   │   ├── models/
│   │   │   ├── request/CompoundInterestRequest.java
│   │   │   └── response/...
│   │   └── exceptions/
│   │
│   ├── clojure/.../service/
│   │   ├── compound_interest_service_idiomatic.clj
│   │   └── compound_interest_service_interop_java.clj
│   │
│   └── resources/
│       └── application.properties
│
└── test/javaGatling/.../performance/
    └── CompoundInterestSimulation.java        # Load testing
```

### Monitoring Stack
- **Prometheus**: JVM metrics collection (CPU, heap, GC)
- **Grafana**: Real-time visualization
- **Gatling**: Load testing and latency analysis

## Technologies
- **Java 17** + **Clojure 1.11.1**
- **Spring Boot 3.4.5** (Web, Validation, Actuator)
- **Gatling 3.9.5** (Performance testing)
- **Prometheus + Grafana** (Metrics)
- **Docker Compose** (Containerization)
- **Maven** (Build)

## Prerequisites
- Java 17+
- Maven 3.8+
- Docker & Docker Compose
- Git

## Quick Start

### 1. Clone Repository
```bash
git clone https://github.com/your-username/java-clojure-performance-analyzer.git
cd java-clojure-performance-analyzer
```

### 2. Run with Docker
```bash
docker compose up -d
```

### 3. Verify Health
```bash
curl http://localhost:8080/actuator/health
```

### 4. Access Interfaces
- **API**: http://localhost:8080
- **Swagger**: http://localhost:8080/swagger-ui.html
- **Prometheus**: http://localhost:9090
- **Grafana**: http://localhost:3000 (admin/admin123)

## API Endpoints

### Java Implementation
```bash
POST /api/compound-interest-java/calculate
Content-Type: application/json

{
  "initialAmount": 10000.0,
  "annualInterestRate": 10.0,
  "years": 100
}
```

### Clojure Idiomatic
```bash
POST /api/compound-interest-clojure/calculate-idiomatic
```

### Clojure Interop Java
```bash
POST /api/compound-interest-clojure/calculate-interop-java
```

### Response Structure
```json
{
  "summary": {
    "initialInvestment": 10000.0,
    "finalBalance": 137806123.40,
    "totalInterestEarned": 137796123.40
  },
  "yearlyDetailsList": [
    {
      "year": 1,
      "startBalance": 10000.0,
      "endBalance": 11000.0,
      "interestEarned": 1000.0
    }
  ]
}
```

## Performance Testing

### Automated Test Suite
```bash
# Run single test with metrics collection
./collect-metrics.sh java
```

### Manual Gatling Execution
```bash
# Inside container
docker exec -it app-performance-test mvn gatling:test
```

### Metrics Collected
- **JVM**: CPU peak, heap usage, GC time/collections
- **Latency**: Min, mean, P50, P95, P99, max
- **Throughput**: Requests/second
- **Reliability**: Success rate, error percentage

### Results Storage
- **Text Reports**: `metrics-results/metrics-{implementation}-{timestamp}.txt`
- **CSV Dataset**: `metrics-results/metrics-comparison.csv`
- **Gatling HTML**: `gatling-results/`

## Paradigm Implementations

### Java (Imperative/OOP)
```java
List<YearlyInvestmentSummary> yearlyDetailsList = new ArrayList<>(years);
double currentAmount = initialAmount;

for (int year = 1; year <= years; year++) {
    final double endBalance = currentAmount * compoundFactor;
    yearlyDetailsList.add(new YearlyInvestmentSummary(...));
    currentAmount = endBalance;
}
```

**Characteristics**: Mutable state, traditional loops, ArrayList allocation

### Clojure Idiomatic (Functional)
```clojure
(loop [year 1
       current-amount initial-amount
       yearly-details-list []]
  (if (> year years)
    yearly-details-list
    (recur (inc year)
           end-balance
           (conj yearly-details-list ...))))
```

**Characteristics**: Immutable structures, tail recursion, persistent vectors

### Clojure Interop Java (Hybrid)
```clojure
(let [^ArrayList yearly-details-list (ArrayList. years)
      final-amount
      (loop [year 1
             current-amount initial-amount]
        (.add yearly-details-list ...)
        (recur (unchecked-inc year) end-balance))]
  ...)
```

**Characteristics**: Java ArrayList for mutation, type hints, unchecked math

## Input Validation
- `initialAmount`: ≥ 0
- `annualInterestRate`: > 0
- `years`: > 0 and ≤ 200

## Development Commands
```bash
# Build
mvn clean install

# Run locally
mvn spring-boot:run

# Compile Clojure
mvn clojure:compile

# Run tests
mvn test

# Docker rebuild
docker compose down -v
docker compose build
docker compose up -d
```

## Environment Variables

### JVM Tuning (docker-compose.yml)
```yaml
JAVA_OPTS: >-
  -Xms4g -Xmx6g
  -XX:+UseG1GC
  -XX:MaxGCPauseMillis=200
  -XX:+HeapDumpOnOutOfMemoryError
```

### Tomcat Configuration (application.properties)
```properties
server.tomcat.threads.max=500
server.tomcat.max-connections=20000
server.tomcat.accept-count=1000
```

## License
This project is licensed under the MIT License.

## Author
Nathan Paiva - TCC Project
