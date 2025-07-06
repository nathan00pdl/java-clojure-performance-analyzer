package com.example.java_clojure_performance_analyzer.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.java_clojure_performance_analyzer.models.request.CompoundInterestRequest;
import com.example.java_clojure_performance_analyzer.models.response.CompoundInterestResponse;
import com.example.java_clojure_performance_analyzer.services.JavaCompoundInterestService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/compound-interest-java")
public class JavaCompoundInterestController {
    private final JavaCompoundInterestService javaService;

    @Autowired
    public JavaCompoundInterestController(@Qualifier("javaImplementation") JavaCompoundInterestService javaService) {
        this.javaService = javaService;
    }

    @PostMapping("/calculate")
    public ResponseEntity<CompoundInterestResponse> calculateCompoundInterest(@Valid @RequestBody CompoundInterestRequest request) {
        CompoundInterestResponse response = javaService.calculateCompoundInterest(request);
        return ResponseEntity.ok(response);
    }
}
