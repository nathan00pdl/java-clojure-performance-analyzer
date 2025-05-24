error id: file://<WORKSPACE>/src/test/scala/com/example/financial_calculator_fp/performance/CompoundInterestSimulation.scala:`<none>`.
file://<WORKSPACE>/src/test/scala/com/example/financial_calculator_fp/performance/CompoundInterestSimulation.scala
empty definition using pc, found symbol in pc: `<none>`.
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -io/gatling/core/Predef.Simulation#
	 -io/gatling/http/Predef.Simulation#
	 -scala/concurrent/duration/Simulation#
	 -Simulation#
	 -scala/Predef.Simulation#
offset: 207
uri: file://<WORKSPACE>/src/test/scala/com/example/financial_calculator_fp/performance/CompoundInterestSimulation.scala
text:
```scala
package com.example.financial_calculator_fp.performance

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class CompoundInterestSimulation extends Simulatio@@n {

  val httpProtocol = http
    .baseUrl("http://localhost:8080") 
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")
  
  val requestBody = StringBody("""
    {
      "initialAmount": 1000.0,
      "annualInterestRate": 8.0,
      "years": 20
    }
  """)

  val javaEndpointScenario = scenario("Java Endpoint Test")
    .exec(
      http("Calculate Compound Interest - Java")
        .post("/api/compound-interest-java/calculate")
        .body(requestBody)
        .check(status.is(200))
    )
    .pause(1)

  val clojureEndpointScenario = scenario("Clojure Endpoint Test")
    .exec(
      http("Calculate Compound Interest - Clojure")
        .post("/api/compound-interest-clojure/calculate")
        .body(requestBody)
        .check(status.is(200))
    )
    .pause(1)

  setUp(
    javaEndpointScenario.inject(
      rampUsers(250).during(30.seconds),
      constantUsersPerSec(10).during(2.minutes)
    ),
    clojureEndpointScenario.inject(
      rampUsers(250).during(30.seconds),
      constantUsersPerSec(10).during(2.minutes)
    )
  ).protocols(httpProtocol)
}
```


#### Short summary: 

empty definition using pc, found symbol in pc: `<none>`.