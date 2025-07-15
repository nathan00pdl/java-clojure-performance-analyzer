package com.example.java_clojure_performance_analyzer.performance;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import java.time.Duration;
import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

public class CompoundInterestSimulation extends Simulation {

    private static final String BASE_URL = "http://localhost:8080";

    private final HttpProtocolBuilder httpProtocol = http
        .baseUrl(BASE_URL)
        .acceptHeader("application/json")
        .contentTypeHeader("application/json")
        .userAgentHeader("Gatling-Performance-Test")
        .connectionHeader("keep-alive")
        .shareConnections() 
        .acceptEncodingHeader("gzip, deflate")
        .disableFollowRedirect()
        .inferHtmlResources()
        .maxConnectionsPerHost(500); 

    private final String simpleCalculation = """
        {
          "initialAmount": 1000.0,
          "annualInterestRate": 5.0,
          "years": 5
        }
        """;

    private final String complexCalculation = """
        {
          "initialAmount": 100000.0,
          "annualInterestRate": 8.0,
          "years": 150
        }
        """;

    // ========== WARM-UP ==========
    
    private final ScenarioBuilder javaWarmUpScenario = scenario("Java Warm-up")
        .exec(
            http("Java Warm-up Simple")
                .post("/api/compound-interest-java/calculate")
                .body(StringBody(simpleCalculation))
                .check(status().is(200))
        )
        .pause(Duration.ofMillis(10), Duration.ofMillis(50))
        .exec(
            http("Java Warm-up Complex")
                .post("/api/compound-interest-java/calculate")
                .body(StringBody(complexCalculation))
                .check(status().is(200))
        )
        .pause(Duration.ofMillis(10), Duration.ofMillis(50));

    private final ScenarioBuilder clojureWarmUpScenario = scenario("Clojure Warm-up")
        .exec(
            http("Clojure Warm-up Simple")
                .post("/api/compound-interest-clojure/calculate")
                .body(StringBody(simpleCalculation))
                .check(status().is(200))
        )
        .pause(Duration.ofMillis(10), Duration.ofMillis(50))
        .exec(
            http("Clojure Warm-up Complex")
                .post("/api/compound-interest-clojure/calculate")
                .body(StringBody(complexCalculation))
                .check(status().is(200))
        )
        .pause(Duration.ofMillis(10), Duration.ofMillis(50));

    // ========== JAVA ==========

    private final ScenarioBuilder javaSimpleScenario = scenario("Java Simple Test")
        .exec(
            http("Java Simple Compound Interest")
                .post("/api/compound-interest-java/calculate")
                .body(StringBody(simpleCalculation))
                .check(status().is(200))
                .check(responseTimeInMillis().saveAs("responseTime"))
        )
        .pause(Duration.ofMillis(5), Duration.ofMillis(20));

    private final ScenarioBuilder javaComplexScenario = scenario("Java Complex Test")
        .exec(
            http("Java Complex Compound Interest")
                .post("/api/compound-interest-java/calculate")
                .body(StringBody(complexCalculation))
                .check(status().is(200))
                .check(responseTimeInMillis().saveAs("responseTime"))
        )
        .pause(Duration.ofMillis(5), Duration.ofMillis(20));

    // ========== CLOJURE ==========

    private final ScenarioBuilder clojureSimpleScenario = scenario("Clojure Simple Test")
        .exec(
            http("Clojure Simple Compound Interest")
                .post("/api/compound-interest-clojure/calculate")
                .body(StringBody(simpleCalculation))
                .check(status().is(200))
                .check(responseTimeInMillis().saveAs("responseTime"))
        )
        .pause(Duration.ofMillis(5), Duration.ofMillis(20));

    private final ScenarioBuilder clojureComplexScenario = scenario("Clojure Complex Test")
        .exec(
            http("Clojure Complex Compound Interest")
                .post("/api/compound-interest-clojure/calculate")
                .body(StringBody(complexCalculation))
                .check(status().is(200))
                .check(responseTimeInMillis().saveAs("responseTime"))
        )
        .pause(Duration.ofMillis(5), Duration.ofMillis(20));

    // ========== EXECUTION TESTS ==========

    {
        setUp(
            clojureWarmUpScenario.injectClosed(
                constantConcurrentUsers(10).during(Duration.ofSeconds(30)),
                rampConcurrentUsers(10).to(50).during(Duration.ofSeconds(30)),
                constantConcurrentUsers(50).during(Duration.ofSeconds(60))
            ).andThen(
                clojureComplexScenario.injectClosed(
                    constantConcurrentUsers(100).during(Duration.ofMinutes(5))
                )
            )
        ).protocols(httpProtocol)
        .assertions(
            global().responseTime().max().lt(5000),
            global().successfulRequests().percent().gt(95.0)
        );
    }

    /*
    ========== OTHER TEST SCENARIOS ==========
    
    // SCENARIO 2: 
    testScenario.injectClosed(
        rampConcurrentUsers(10).to(200).during(Duration.ofMinutes(5)),
        constantConcurrentUsers(200).during(Duration.ofMinutes(10))
    )
    
    // SCENARIO 3: 
    testScenario.injectClosed(
        rampConcurrentUsers(10).to(500).during(Duration.ofMinutes(2)),
        constantConcurrentUsers(500).during(Duration.ofMinutes(3)),
        rampConcurrentUsers(500).to(1000).during(Duration.ofMinutes(2)),
        constantConcurrentUsers(1000).during(Duration.ofMinutes(3))
    )
    
    // SCENARIO 4: 
    testScenario.injectClosed(
        rampConcurrentUsers(100).to(2000).during(Duration.ofMinutes(10)),
        constantConcurrentUsers(2000).during(Duration.ofMinutes(15))
    )
    */
}