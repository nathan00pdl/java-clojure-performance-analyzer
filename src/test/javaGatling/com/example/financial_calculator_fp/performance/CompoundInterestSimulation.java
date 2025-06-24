package com.example.financial_calculator_fp.performance;

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
        .userAgentHeader("Gatling-Extreme-Test")
        .connectionHeader("keep-alive")
        .shareConnections() 
        .acceptEncodingHeader("gzip, deflate")
        .disableFollowRedirect(); 

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

    private final ScenarioBuilder javaSimpleScenario = scenario("Java endpoint - simple test")
        .exec(
            http("Java Simple Compound Interest")
                .post("/api/compound-interest-java/calculate")
                .body(StringBody(simpleCalculation))
                .check(status().is(200))
                .check(responseTimeInMillis().saveAs("javaResponseTime"))
        )
        .pause(Duration.ofMillis(5), Duration.ofMillis(20));

    private final ScenarioBuilder javaComplexScenario = scenario("Java endpoint - complex test")
        .exec(
            http("Java Complex Compound Interest")
                .post("/api/compound-interest-java/calculate")
                .body(StringBody(complexCalculation))
                .check(status().is(200))
                .check(responseTimeInMillis().saveAs("javaComplexResponseTime"))
        )
        .pause(Duration.ofMillis(5), Duration.ofMillis(20));

    private final ScenarioBuilder clojureSimpleScenario = scenario("Clojure endpoint - simple test")
        .exec(
            http("Clojure Simple Compound Interest")
                .post("/api/compound-interest-clojure/calculate")
                .body(StringBody(simpleCalculation))
                .check(status().is(200))
                .check(responseTimeInMillis().saveAs("clojureResponseTime"))
        )
        .pause(Duration.ofMillis(5), Duration.ofMillis(20));


    private final ScenarioBuilder clojureComplexScenario = scenario("Clojure endpoint - complex test")
        .exec(
            http("Clojure Complex Compound Interest")
                .post("/api/compound-interest-clojure/calculate")
                .body(StringBody(complexCalculation))
                .check(status().is(200))
                .check(responseTimeInMillis().saveAs("clojureComplexResponseTime"))
        )
        .pause(Duration.ofMillis(5), Duration.ofMillis(20));

    
    {
        setUp(
            clojureComplexScenario.injectClosed(
                rampConcurrentUsers(1000).to(30000).during(Duration.ofMinutes(5)),
                rampConcurrentUsers(30000).to(50000).during(Duration.ofMinutes(5))
            )
        ).protocols(httpProtocol);
    }

    /*
    ----------------------------------------TESTS-----------------------------------------------------

    X = javaSimpleScenario || javaComplexScenario || clojureSimpleScenario || clojureComplexScenario
    Y = javaComplexScenario || clojureComplexScenario

    --------------------------------------SCENARIO 1-------------------------------------------------

    {
        setUp(
            X.injectClosed(
                constantConcurrentUsers(10000).during(Duration.ofMinutes(10))
            )
        ).protocols(httpProtocol);
    }

    --------------------------------------SCENARIO 2-------------------------------------------------
    
    {
        setUp(
            X.injectClosed(
                rampConcurrentUsers(10000).to(20000).during(Duration.ofMinutes(10))
            )
        ).protocols(httpProtocol);
    }
    
    --------------------------------------SCENARIO 3-------------------------------------------------

    {
        setUp(
            X.injectClosed(
                rampConcurrentUsers(1000).to(30000).during(Duration.ofMinutes(10)),
                rampConcurrentUsers(30000).to(50000).during(Duration.ofMinutes(10)),
                rampConcurrentUsers(50000).to(1000).during(Duration.ofMinutes(5))
            )
        ).protocols(httpProtocol);
    }

        --------------------------------------SCENARIO 4-------------------------------------------------

    {
        setUp(
            Y.injectClosed(
                rampConcurrentUsers(1000).to(30000).during(Duration.ofMinutes(5)),
                rampConcurrentUsers(30000).to(50000).during(Duration.ofMinutes(5))
            )
        ).protocols(httpProtocol);
    }
    */
}