package com.example.financial_calculator_fp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import com.example.financial_calculator_fp.services.CompoundInterestService;

import clojure.java.api.Clojure;
import clojure.lang.IFn;

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
    private static final String CLOJURE_NAMESPACE = "com.example.financial-calculator-fp.service.compound-interest-service";
    private static final String CREATE_SERVICE_FUNCTION = "create-service";

    @Bean("clojureImplementation")
    @DependsOn("clojureNamespaceLoader")
    public CompoundInterestService compoundInterestService() {
        try {
            IFn createService = Clojure.var(CLOJURE_NAMESPACE, CREATE_SERVICE_FUNCTION);
            
            if (createService == null) {
                throw new RuntimeException("Could Find Clojure Function: " + CREATE_SERVICE_FUNCTION);
            }

            Object result = createService.invoke();

            if (!(result instanceof CompoundInterestService)) {
                throw new RuntimeException("Clojure Function Did Not Return Expected Service Type!");
            }

            return (CompoundInterestService) result;

        } catch (Exception e) {
            throw new RuntimeException("Failed To Create Clojure Service Implementation!", e);
        }
    }
}
