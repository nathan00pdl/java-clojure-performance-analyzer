package com.example.financial_calculator_fp.services;

import com.example.financial_calculator_fp.models.request.CompoundInterestRequestDTO;
import com.example.financial_calculator_fp.models.response.CompoundInterestResponseDTO;

public interface CompoundInterestService {
    CompoundInterestResponseDTO calculateCompoundInterest(CompoundInterestRequestDTO request);
}
