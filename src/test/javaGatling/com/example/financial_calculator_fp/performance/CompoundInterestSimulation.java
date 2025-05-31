package com.example.financial_calculator_fp.performance;

import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.core.CoreDsl.constantConcurrentUsers;
import static io.gatling.javaapi.core.CoreDsl.constantUsersPerSec;
import static io.gatling.javaapi.core.CoreDsl.rampConcurrentUsers;
import static io.gatling.javaapi.core.CoreDsl.rampUsers;
import static io.gatling.javaapi.core.CoreDsl.rampUsersPerSec;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

import java.time.Duration;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

public class CompoundInterestSimulation extends Simulation {

    HttpProtocolBuilder httpProtocol = http
        .baseUrl("http://localhost:8080")
        .acceptHeader("application/json")
        .contentTypeHeader("application/json");

    String requestBody = "{\n" +
        " \"initialAmount\": 1000.0,\n" + 
        " \"annualInterestRate\": 8.0,\n" + 
        " \"years\": 50\n" + 
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
            clojureEndpointScenario.injectClosed(
                rampConcurrentUsers(500).to(30000).during(Duration.ofMinutes(10)),
                constantConcurrentUsers(30000).during(Duration.ofMinutes(5))
            )
        ).protocols(httpProtocol);
    }

    /*
    {
        setUp(
            javaEndpointScenario.injectClosed(
                rampConcurrentUsers(500).to(30000).during(Duration.ofMinutes(10)),
                constantConcurrentUsers(30000).during(Duration.ofMinutes(5))
            )
        ).protocols(httpProtocol);
    }
    */
}