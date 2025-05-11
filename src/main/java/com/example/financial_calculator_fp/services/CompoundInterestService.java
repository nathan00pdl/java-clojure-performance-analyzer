package com.example.financial_calculator_fp.services;

import com.example.financial_calculator_fp.models.request.CompoundInterestRequestDTO;
import com.example.financial_calculator_fp.models.response.CompoundInterestResponseDTO;

/*
 * Note:
 * 
 * This Interface will be implemented by Clojure code.
 */

public interface CompoundInterestService {
    CompoundInterestResponseDTO calculateCompoundInterest(CompoundInterestRequestDTO request);
}
