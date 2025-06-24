package com.example.financial_calculator_fp.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    
    private static final Logger logger = LoggerFactory.getLogger(ClojureConfiguration.class);
    private static final String CLOJURE_NAMESPACE = "com.example.financial-calculator-fp.service.compound-interest-service";
    private static final String CREATE_SERVICE_FUNCTION = "create-service";

    @Bean("clojureImplementation")
    @DependsOn("clojureNamespaceLoader")
    public CompoundInterestService compoundInterestService() {
        try {
            logger.debug("Creating Clojure service implementation");
            
            // Get the Clojure function
            IFn createService = Clojure.var(CLOJURE_NAMESPACE, CREATE_SERVICE_FUNCTION);
            
            if (createService == null) {
                String errorMsg = String.format("Could NOT find Clojure function '%s' in namespace '%s'", 
                                              CREATE_SERVICE_FUNCTION, CLOJURE_NAMESPACE);
                logger.error(errorMsg);
                throw new RuntimeException(errorMsg);
            }

            // Invoke the function to create service
            Object result = createService.invoke();

            // Validate the result type
            if (!(result instanceof CompoundInterestService)) {
                String errorMsg = String.format("Clojure function returned unexpected type. Expected: %s, Got: %s", 
                                              CompoundInterestService.class.getSimpleName(), 
                                              result != null ? result.getClass().getSimpleName() : "null");
                logger.error(errorMsg);
                throw new RuntimeException(errorMsg);
            }

            logger.info("Clojure service implementation created successfully");
            return (CompoundInterestService) result;

        } catch (Exception e) {
            logger.error("Failed to create Clojure service implementation", e);
            throw new RuntimeException("Failed to create Clojure service implementation", e);
        }
    }
}

