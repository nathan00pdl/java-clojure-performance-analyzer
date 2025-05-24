package com.example.financial_calculator_fp.performance;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;
import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import java.time.Duration;

public class CompoundInterestSimulation extends Simulation{
    HttpProtocolBuilder httpProtocolBuilder = http
        .baseUrl("http://localhost:8080")
        .acceptHeader("application/json")
        .contentTypeHeader("application/json");

    String requestBody = "{\n" +
        " \"initialAmount\": 1000.0,\n" + 
        " \"annualInterestRate\": 8.0,\n" + 
        " \"years\": 20\n" + 
        "}";

    ScenarioBuilder javaEndpointScenario = scenario("JAVA Endpoint Test")
        .exec(
            http("Calculate Compound Interest - JAVA")
                .post("/api/compound-interest-java/calculate")
                .body(StringBody(requestBody))
                .check(status().is(200))
        )
        .pause(1);
    
    ScenarioBuilder clojureEndpointScenario = scenario("CLOJURE Endpoint Test")
        .exec(
            http("Calculate Compound Interest - CLOJURE")
                .post("/api/compound-interest-clojure/calculate")
                .body(StringBody(requestBody))
                .check(status().is(200))
        )
        .pause(1);

    {
        setUp(
            javaEndpointScenario.injectOpen(
                rampUsers(250).during(Duration.ofSeconds(30)),
                constantUsersPerSec(10).during(Duration.ofMinutes(2))
            ),
            clojureEndpointScenario.injectOpen(
                rampUsers(250).during(Duration.ofSeconds(30)),
                constantUsersPerSec(10).during(Duration.ofMinutes(2))
            )
        ).protocols(httpProtocol);
    }
}
