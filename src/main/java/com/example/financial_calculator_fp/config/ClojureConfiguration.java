package com.example.financial_calculator_fp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.financial_calculator_fp.services.CompoundInterestService;

import clojure.java.api.Clojure;
import clojure.lang.IFn;

/*
 * Notes:
 * 
 * Component responsible for allowing services implemented in Clojure to be exposed as Spring beans, 
 * allowing dependency injection into controllers and other Java components.
 */

@Configuration
public class ClojureConfiguration {
    @Bean
    public CompoundInterestService compoundInterestService() {
        IFn createService = Clojure.var("com.example.financial-calculator-fp.service.compound-interest-service", "create-service");

        return (CompoundInterestService) createService.invoke();
    }
}
