package com.example.financial_calculator_fp.config;

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
    @PostConstruct
    public void loadNamespaces() {
        IFn require = Clojure.var("clojure.core", "require"); 
        require.invoke(Clojure.read("com.example.financial-calculator-fp.service.compound-interest-service"));
 
        System.out.println("Namespaces Clojure carregados com sucesso");
    }
}
