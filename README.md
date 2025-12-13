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
    └── CompoundInterestSimulation.java        # Load testing configuration
```

### Monitoring Stack
- **Prometheus**: JVM metrics collection (CPU, heap, GC)
- **Grafana**: Real-time visualization
- **Gatling**: Load testing and latency analysis
- **Docker Compose**: Isolated test environments

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

## Performance Testing Workflow

This project follows a rigorous testing methodology to ensure reliable, reproducible results for academic analysis. The workflow consists of executing isolated tests for each implementation, guaranteeing no metric contamination between executions. Before each test, containers and volumes are completely removed to ensure a clean environment. After recreating the containers, a stabilization period is observed before collecting initial Prometheus metrics including GC time, GC collections, and request count. The collect-metrics script then waits for manual Gatling test execution in a separate terminal. After Gatling completes, the script waits 15 seconds for Prometheus synchronization before collecting final metrics. The script automatically calculates differences between initial and final metrics, extracts data from Gatling's generated stats.json file, and saves everything in two formats: a detailed text file and a CSV dataset. Between tests of different implementations, a 5-minute pause ensures the system returns to idle state before starting the next cycle. Results are validated by analyzing the coefficient of variation of CPU Peak across tests, targeting values below 10% for scientific consistency.

### Test Configuration

Edit the Gatling simulation to configure your test scenario:
```java
// src/test/javaGatling/.../CompoundInterestSimulation.java

setUp(
    scenarioJava.injectOpen(                                    // Choose scenario
        constantUsersPerSec(100).during(Duration.ofMinutes(5))  // Load configuration
    )
).protocols(httpProtocol);
```

**Available scenarios:**
- `scenarioJava` - Java OOP implementation
- `scenarioClojureIdiomatic` - Clojure FP implementation
- `scenarioClojureInteropJava` - Hybrid implementation

### Step-by-Step Testing Protocol

#### 1. Configure Test Scenario
```bash
# Edit CompoundInterestSimulation.java to select implementation and load
vim src/test/javaGatling/com/example/java_clojure_performance_analyzer/performance/CompoundInterestSimulation.java
```

#### 2. Build Application (if code changed)
```bash
docker compose build app
```

#### 3. Clean Environment
```bash
docker compose down -v && docker system prune -f && docker volume prune -f
```

#### 4. Start Services and Verify Health
```bash
docker compose up -d app prometheus && sleep 30 && curl -sf http://localhost:8080/actuator/health
```

#### 5. Verify Test Configuration
```bash
grep -A 5 "setUp" src/test/javaGatling/com/example/java_clojure_performance_analyzer/performance/CompoundInterestSimulation.java | grep -v "//"
```

#### 6. Check System Baseline
```bash
# CPU idle percentage (should be > 95%)
mpstat 1 1 | awk '/Average:/ {print "CPU idle:", $NF "%"}'

# Available RAM percentage (should be > 75%)
free | awk '/^Mem:/ {printf "Available RAM: %.1f%%\n", ($7/$2)*100}'

# Top processes overview
top -b -n 1 | head -n 20
```

#### 7. Run Test with Metrics Collection
```bash
# Terminal 1: Start metrics collection
./collect-metrics.sh java  # or clojure-idiomatic or clojure-interop-java

# Terminal 2: Execute load test when prompted
./run-gatling.sh

# Return to Terminal 1 and press ENTER after Gatling finishes
```

#### 8. Wait for System Stabilization
```bash
sleep 300  # 5 minutes between tests
```

#### 9. Repeat for Other Implementations
Repeat steps 3-8 for each implementation, changing the scenario in CompoundInterestSimulation.java and the argument to collect-metrics.sh.

### Verify Results
```bash
# List generated Gatling reports
ls -lt gatling-results/

# View latest metrics
cat metrics-results/metrics-comparison.csv

# Check individual test report
cat metrics-results/metrics-{implementation}-{timestamp}.txt
```

## Metrics Collected

### Prometheus Metrics
- **CPU Peak**: Maximum CPU usage during test
- **Heap Peak**: Maximum heap memory allocation
- **GC Time**: Total garbage collection time
- **GC Collections**: Number of GC cycles
- **Total Requests**: HTTP requests processed

### Gatling Metrics
- **Response Time**: Min, Mean, P50, P75, P95, P99, Max
- **Throughput**: Requests per second
- **Success Rate**: Percentage of successful requests
- **Response Distribution**: Percentage under 800ms, 800-1200ms, >1200ms

### Results Storage
- **Text Reports**: `metrics-results/metrics-{implementation}-{timestamp}.txt`
- **CSV Dataset**: `metrics-results/metrics-comparison.csv`
- **Gatling HTML**: `gatling-results/compoundinterestsimulation-{timestamp}/`

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

## Configuration

### JVM Tuning (docker-compose.yml)
```yaml
JAVA_OPTS: >-
  -Xms4g                              # Initial heap size
  -Xmx6g                              # Maximum heap size
  -Xss512k                            # Thread stack size
  -XX:MaxMetaspaceSize=512m           # Maximum metaspace (class metadata)
  -XX:+UseG1GC                        # Use G1 garbage collector
  -XX:MaxGCPauseMillis=200            # Target maximum GC pause time
  -XX:+ParallelRefProcEnabled         # Parallel reference processing
  -XX:+UseStringDeduplication         # Deduplicate identical strings
  -XX:+UseCompressedOops              # Compress object pointers (memory efficiency)
  -XX:+HeapDumpOnOutOfMemoryError     # Generate heap dump on OOM
  -XX:HeapDumpPath=/app/heap-dumps    # Heap dump storage location
```

### Tomcat Configuration (application.properties)
```properties
server.tomcat.threads.max=500
server.tomcat.max-connections=20000
server.tomcat.accept-count=1000
```

### Prometheus Scraping (monitoring-configs/prometheus.yml)
```yaml
scrape_interval: 5s
evaluation_interval: 5s
scrape_timeout: 4s
```

## Troubleshooting

### Container Not Healthy
```bash
# Check logs
docker logs app-performance-test

# Restart services
docker compose restart app
```

### Prometheus Not Scraping
```bash
# Verify endpoint
curl http://localhost:8080/actuator/prometheus

# Check Prometheus targets
open http://localhost:9090/targets
```

### Gatling Fails to Execute
```bash
# Ensure app is healthy first
curl http://localhost:8080/actuator/health

# Check container resources
docker stats app-performance-test
```

## License
This project is licensed under the MIT License.

## Author
Nathan Paiva - TCC Project
