package com.example.financial_calculator_fp.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.financial_calculator_fp.services.CompoundInterestService;

import clojure.java.api.Clojure;
import clojure.lang.IFn;
import jakarta.annotation.PostConstruct;

/**
 * Configuration class for integrating Clojure services with Spring Boot framework.
 * 
 * This configuration loads the necessary Clojure namespaces and exposes Clojure 
 * services as Spring Beans, enabling dependency injection into controllers and 
 * other Java components.
 */

@Configuration
public class ClojureConfiguration {
    
    private static final Logger logger = LoggerFactory.getLogger(ClojureConfiguration.class);
    private static final String NAMESPACE = "com.example.financial-calculator-fp.service.compound-interest-service";
    private static final String FUNCTION = "create-service";

    @PostConstruct
    public void loadNamespaces() {
        try {
            logger.info("Loading Clojure Namespace: {}", NAMESPACE);
            
            IFn require = Clojure.var("clojure.core", "require");
            require.invoke(Clojure.read(NAMESPACE));
            
            logger.info("Clojure Namespace Loaded Successfully: {}", NAMESPACE);
            
        } catch (Exception e) {
            logger.error("Failed To Load Clojure Namespace: {}", NAMESPACE, e);
            throw new RuntimeException("Critical Error: Unable To Load Clojure Namespace", e);
        }
    }

    @Bean("clojureImplementation")
    public CompoundInterestService compoundInterestService() {
        try {
            logger.debug("Creating Clojure ServiceIimplementation");
            
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