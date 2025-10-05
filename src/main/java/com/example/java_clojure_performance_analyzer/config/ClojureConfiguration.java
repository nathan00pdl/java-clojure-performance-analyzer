package com.example.java_clojure_performance_analyzer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.java_clojure_performance_analyzer.services.CompoundInterestService;

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
    private static final String IDIOMATIC_NAMESPACE = "com.example.java-clojure-performance-analyzer.service.compound-interest-service-idiomatic";
    private static final String OPTIMIZED_NAMESPACE = "com.example.java-clojure-performance-analyzer.service.compound-interest-service-optimized";
    private static final String FUNCTION = "create-service";

    private IFn idiomaticServiceFactory;
    private IFn optimizedServiceFactory;

    @PostConstruct
    public void loadNamespaces() {
        IFn require = Clojure.var("clojure.core", "require");
        require.invoke(Clojure.read(IDIOMATIC_NAMESPACE));
        require.invoke(Clojure.read(OPTIMIZED_NAMESPACE));

        this.idiomaticServiceFactory = Clojure.var(IDIOMATIC_NAMESPACE, FUNCTION);
        this.optimizedServiceFactory = Clojure.var(OPTIMIZED_NAMESPACE, FUNCTION);
    }

    @Bean("clojureIdiomaticImplementation")
    public CompoundInterestService compoundInterestService() {
        return (CompoundInterestService) idiomaticServiceFactory.invoke();
    }

    @Bean("clojureOptimizedImplementation")
    public CompoundInterestService compoundInterestOptimizedService() {
        return (CompoundInterestService) optimizedServiceFactory.invoke();
    }
}