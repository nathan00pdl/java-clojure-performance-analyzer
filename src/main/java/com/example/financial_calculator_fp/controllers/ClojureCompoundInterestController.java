package com.example.financial_calculator_fp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.financial_calculator_fp.models.request.CompoundInterestRequestDTO;
import com.example.financial_calculator_fp.models.response.CompoundInterestResponseDTO;
import com.example.financial_calculator_fp.services.CompoundInterestService;

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
    public ResponseEntity<CompoundInterestResponseDTO> calculateCompoundInterest (@Valid @RequestBody CompoundInterestRequestDTO request) {
        if (request.getYears() > 200) {
            throw new com.example.financial_calculator_fp.exceptions.ValidationException("years", "The Maximium Period Allowed Is 200 Years!");
        }
       
        CompoundInterestResponseDTO response = compoundInterestService.calculateCompoundInterest(request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Compound Interest Service Is Working!");
    }
}
