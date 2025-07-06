package com.example.java_clojure_performance_analyzer.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.java_clojure_performance_analyzer.models.request.CompoundInterestRequest;
import com.example.java_clojure_performance_analyzer.models.response.CompoundInterestResponse;
import com.example.java_clojure_performance_analyzer.services.CompoundInterestService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/compound-interest-clojure")
public class ClojureCompoundInterestController {
    private CompoundInterestService compoundInterestService;

    @Autowired
    public ClojureCompoundInterestController(@Qualifier("clojureImplementation") CompoundInterestService compoundInterestService) {
        this.compoundInterestService = compoundInterestService;
    }

    @PostMapping("/calculate")
    public ResponseEntity<CompoundInterestResponse> calculateCompoundInterest (@Valid @RequestBody CompoundInterestRequest request) {
        CompoundInterestResponse response = compoundInterestService.calculateCompoundInterest(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Compound Interest Service Is Working!");
    }
}
