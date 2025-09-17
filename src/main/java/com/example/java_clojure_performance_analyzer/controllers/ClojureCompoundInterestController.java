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
    private final CompoundInterestService idiomaticService;
    private final CompoundInterestService optimizedService;

    @Autowired
    public ClojureCompoundInterestController(
            @Qualifier("clojureIdiomaticImplementation") CompoundInterestService idioInterestService,
            @Qualifier("clojureOptimizedImplementation") CompoundInterestService optimizedService) {
        this.idiomaticService = idioInterestService;
        this.optimizedService = optimizedService;
    }

    @PostMapping("/calculate-idiomatic")
    public ResponseEntity<CompoundInterestResponse> calculateCompoundInterestIdiomatic(@Valid @RequestBody CompoundInterestRequest request) {
        CompoundInterestResponse response = idiomaticService.calculateCompoundInterest(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/calculate-optimized")
    public ResponseEntity<CompoundInterestResponse> calculateCompoundInterestOptimized(@Valid @RequestBody CompoundInterestRequest request) {
        CompoundInterestResponse response = optimizedService.calculateCompoundInterest(request);
        return ResponseEntity.ok(response);
    }
}
