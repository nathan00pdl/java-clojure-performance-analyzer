package com.example.financial_calculator_fp.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import clojure.java.api.Clojure;
import clojure.lang.IFn;
import jakarta.annotation.PostConstruct;

/*
 * Notes:
 * 
 * Component responsible for loading the necessary Clojure namespaces when the application starts.
 * 
 * The loadNamespaces method is loaded after the bean is constructed.
 * 
 * IFn is the Java interface (represents a Clojure function) that allows to invoke  functions from Java code. 
 * The "require" parameter refers to a function that allows loadind Clojure namespaces
 */

@Component
public class ClojureNamespaceLoader {
    
    private static final Logger logger = LoggerFactory.getLogger(ClojureNamespaceLoader.class);
    private static final String CLOJURE_NAMESPACE = "com.example.financial-calculator-fp.service.compound-interest-service";
    
    @PostConstruct
    public void loadNamespaces() {
        try {
            logger.info("Loading Clojure namespace: {}", CLOJURE_NAMESPACE);
            
            IFn require = Clojure.var("clojure.core", "require");
            require.invoke(Clojure.read(CLOJURE_NAMESPACE));
            
            logger.info("Clojure namespace loaded successfully: {}", CLOJURE_NAMESPACE);
            
        } catch (Exception e) {
            logger.error("Failed to load Clojure namespace: {}", CLOJURE_NAMESPACE, e);
            throw new RuntimeException("Critical error: Unable to load Clojure namespace", e);
        }
    }
}