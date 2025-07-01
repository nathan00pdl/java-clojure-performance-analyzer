package com.example.financial_calculator_fp.services;

import com.example.financial_calculator_fp.models.request.CompoundInterestRequest;
import com.example.financial_calculator_fp.models.response.CompoundInterestResponse;

public interface CompoundInterestService {
    CompoundInterestResponse calculateCompoundInterest(CompoundInterestRequest request);
}
