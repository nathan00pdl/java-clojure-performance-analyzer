package com.example.financial_calculator_fp.performance;

import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.core.CoreDsl.constantUsersPerSec;
import static io.gatling.javaapi.core.CoreDsl.rampUsers;
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
                rampUsers(1250).during(Duration.ofSeconds(30)),
                constantUsersPerSec(30).during(Duration.ofMinutes(2))
            ),
            clojureEndpointScenario.injectOpen(
                rampUsers(1250).during(Duration.ofSeconds(30)),
                constantUsersPerSec(30).during(Duration.ofMinutes(2))
            )
        ).protocols(httpProtocol);
    }
}