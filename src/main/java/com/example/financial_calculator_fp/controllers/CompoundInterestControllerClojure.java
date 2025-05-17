package com.example.financial_calculator_fp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
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
public class CompoundInterestControllerClojure {
    private CompoundInterestService compoundInterestService;

    @Autowired
    public CompoundInterestControllerClojure(CompoundInterestService compoundInterestService) {
        this.compoundInterestService = compoundInterestService;
    }

    @PostMapping("/calculate")
    public ResponseEntity<CompoundInterestResponseDTO> calculateCompoundInterest (@Valid @RequestBody CompoundInterestRequestDTO request) {
        if (request.getYears() > 50) {
            throw new com.example.financial_calculator_fp.exceptions.ValidationException("years", "O período máximo permitido é de 50 anos");
        }
       
        CompoundInterestResponseDTO response = compoundInterestService.calculateCompoundInterest(request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Serviço de Juros Compostos está funcionando!");
    }
}
