# Performance Analyzer: Programming Paradigms Comparison

## Overview
Comparative performance study between programming paradigms through a REST API that calculates compound interest. The project implements three distinct approaches:

- **Java (OOP)**: Pure object-oriented implementation using imperative style with mutable `ArrayList`
- **Clojure Idiomatic (FP)**: Pure functional implementation with immutable `PersistentVector` data structures
- **Clojure Interop Java (Hybrid OOP+FP)**: Strategic Java interoperability using native `ArrayList` within Clojure syntax

## Research Objective
Measure and compare quantitatively the performance cost of Clojure's idiomatic abstractions versus Java in HTTP request processing on the JVM, and investigate the role of Garbage Collector behavior as the mediating mechanism between paradigm choice and observed latency. The central finding of the study is that Java exhibits structural instability under saturation at 1000 req/s (CV% ~31%), while both Clojure implementations show convergent latency with greater stability (CV% ~13–15%). The generational hypothesis — that Clojure Idiomatic's short-lived immutable objects align with the design assumptions of generational GC — is the explanatory framework, not a claim of Clojure superiority.

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

This project follows a rigorous testing methodology to ensure reliable, reproducible results for academic analysis. The study comprised **three independent test rounds** (V1, V2, V3), each organized as 3 implementations × 3 load levels × up to 7 repetitions per group.

- **V1 (initial round, 54 executions)**: Used as historical reference for 100 and 500 req/s. A bug was identified in the `metrics-collection.sh` script where PromQL queries for GC Time and GC Collections used `result[0]`, capturing only one G1GC series instead of the sum of all. This caused underestimation of absolute GC values. Relative ordering between implementations was preserved.
- **V2 (corrected round, 54 executions)**: Conducted with the corrected script (replacing `result[0]` with `[.data.result[].value[1] | tonumber] | add // 0`). Constitutes the definitive data for 100 and 500 req/s.
- **V3 (dedicated round, 21 executions)**: Dedicated exclusively to 1000 req/s, motivated by instability observed in V2 at that load level (4 collapses across 18 executions). Constitutes the definitive data for 1000 req/s.

Executions were excluded under two independent criteria:
1. **Qualitative invalidity**: error rate > 0% — sufficient condition for discard regardless of other metrics.
2. **Statistical outliers**: z-score criterion (|z| > 2) applied over the P95 latency metric.

Between consecutive executions, a minimum 300-second stabilization interval was observed.

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
./metrics-collection.sh java  # or clojure-idiomatic or clojure-interop-java

# Terminal 2: Execute load test when prompted
./run-gatling.sh

# Return to Terminal 1 and press ENTER after Gatling finishes
```

#### 8. Wait for System Stabilization
```bash
sleep 300  # 5 minutes between tests
```

#### 9. Repeat for Other Implementations
Repeat steps 3-8 for each implementation, changing the scenario in `CompoundInterestSimulation.java` and the argument to `metrics-collection.sh`.

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
All Prometheus metrics are calculated as the delta between the initial value (collected before the test starts) and the final value (collected after a 15-second synchronization wait post-Gatling), isolating consumption attributable exclusively to the test period.

- **CPU Peak**: Maximum CPU usage during test
- **Heap Peak**: Maximum heap memory allocation (GB and % of configured 6GB max)
- **GC Time**: Total garbage collection pause time (`jvm_gc_pause_seconds_sum`)
- **GC Collections**: Number of GC cycles (`jvm_gc_pause_seconds_count`)
- **Total Requests**: HTTP requests processed

### Gatling Metrics
- **Response Time**: Min, Mean, P50, P75, P95, P99, Max (ms)
- **Throughput**: Requests per second
- **Success Rate**: Percentage of successful requests (executions with error rate > 0% are discarded)
- **Response Distribution**: Percentage under 800ms, 800–1200ms, >1200ms

P95 and P99 are the primary latency metrics, as they capture tail behavior under sustained load and are most sensitive to GC pause impact.

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

**Characteristics**: Mutable state, traditional loops, single `ArrayList` instance mutated in place, arithmetic on primitive `double`/`int` types — no intermediate object versions created per iteration.

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

**Characteristics**: Immutable `PersistentVector` expanded via `conj` at each iteration, producing intermediate object versions that become GC-eligible immediately. Arithmetic intermediated by `clojure.lang.Numbers` (56 `invokestatic` calls per request vs. Java's 7). Higher GC pressure across all load levels, but this does not translate into latency penalties in stable executions.

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

**Characteristics**: Native Java `ArrayList` with `^ArrayList` type hint (eliminates runtime reflection), `unchecked-inc` for overflow-check-free counter increment, explicit primitive coercions. Allocation pattern approximates Java imperative. Retains Clojure runtime infrastructure for type conversion operations.

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
Nathan Paiva — Undergraduate Thesis (Computer Engineering, IFSP Piracicaba)