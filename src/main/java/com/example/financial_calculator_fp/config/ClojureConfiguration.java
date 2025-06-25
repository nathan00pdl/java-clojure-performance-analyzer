package com.example.financial_calculator_fp.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import com.example.financial_calculator_fp.services.CompoundInterestService;

import clojure.java.api.Clojure;

/*
 * Notes:
 * 
 * Configuration class for integrating Clojure services with Spring Boot framework.
 * 
 * This configuration allows services implemented in clojure to be exposed as Spring Beans, 
 * enabling dependency injection into controllers and other Java component.
 * 
 * The bean creation depends on namespace loading to ensure Clojure code is properly initilized.
 */

@Configuration
public class ClojureConfiguration {
    
    private static final Logger logger = LoggerFactory.getLogger(ClojureConfiguration.class);
    private static final String NAMESPACE = "com.example.financial-calculator-fp.service.compound-interest-service";
    private static final String FUNCTION = "create-service";

    @Bean("clojureImplementation")
    @DependsOn("clojureNamespaceLoader")
    public CompoundInterestService compoundInterestService() {
        try {
            logger.debug("Creating Clojure Service Implementation");
            
            Object service = Clojure.var(NAMESPACE, FUNCTION).invoke();
            
            if (!(service instanceof CompoundInterestService)) {
                throw new RuntimeException("Invalid Service Type Returned From Clojure");
            }

            logger.info("Clojure Service Created Successfully");
            return (CompoundInterestService) service;
            
        } catch (Exception e) {
            logger.error("Failed To Create Clojure Service", e);
            throw new RuntimeException("Clojure Service Creation Failed", e);
        }
    }
}

