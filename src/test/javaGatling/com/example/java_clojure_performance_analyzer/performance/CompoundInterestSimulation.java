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
        .maxConnectionsPerHost(1000); 

    private final String requestBody = """
        {
          "initialAmount": 10000.0,
          "annualInterestRate": 10.0,
          "years": 100
        }
        """;

    // JAVA 

    private final ScenarioBuilder scenarioJava = scenario("JAVA - Test")
        .exec(
            http("Calculate Compound Interest: Java")
                .post("/api/compound-interest-java/calculate")
                .body(StringBody(requestBody))
                .check(status().is(200))
                .check(responseTimeInMillis().saveAs("responseTime"))
        )
        .pause(Duration.ofMillis(5), Duration.ofMillis(20));

    // CLOJURE IDIOMATIC

    private final ScenarioBuilder scenarioClojureIdiomatic = scenario("CLOJURE IDIOMATIC - Test")
        .exec(
            http("Calculate Compound Interest: Clojure Idiomatic")
                .post("/api/compound-interest-clojure/calculate-idiomatic")
                .body(StringBody(requestBody))
                .check(status().is(200))
                .check(responseTimeInMillis().saveAs("responseTime"))
        )
        .pause(Duration.ofMillis(5), Duration.ofMillis(20));

    // CLOJURE OTIMIZED

    private final ScenarioBuilder scenarioClojureOptimized = scenario("CLOJURE OPTIMIZED - Test")
        .exec(
            http("Calculate Compound Interest: Clojure Optimized")
                .post("/api/compound-interest-clojure/calculate-optimized")
                .body(StringBody(requestBody))
                .check(status().is(200))
                .check(responseTimeInMillis().saveAs("responseTime"))
        )
        .pause(Duration.ofMillis(5), Duration.ofMillis(20));


    // EXECUTION TEST

    {
        setUp(
            scenarioClojureIdiomatic.injectClosed(
                constantConcurrentUsers(100).during(Duration.ofMinutes(5))
            )
        ).protocols(httpProtocol);
    }
}


/*
{
    setUp(
        scenarioClojureIdiomatic.injectClosed(
            constantConcurrentUsers(1000).during(Duration.ofMinutes(2))
        )
    ).protocols(httpProtocol);
}

{
    setUp(
        scenarioClojureOptimized.injectClosed(
            constantConcurrentUsers(1000).during(Duration.ofMinutes(2))
        )
    ).protocols(httpProtocol);
}

{
    setUp(
        scenario.injectClosed(
            rampConcurrentUsers(10).to(500).during(Duration.ofMinutes(2)),
            constantConcurrentUsers(500).during(Duration.ofMinutes(3)),
            rampConcurrentUsers(500).to(1000).during(Duration.ofMinutes(2)),
            constantConcurrentUsers(1000).during(Duration.ofMinutes(3))
        )
    ).protocols(httpProtocol);
}
*/