package com.example.java_clojure_performance_analyzer.services;

import com.example.java_clojure_performance_analyzer.models.request.CompoundInterestRequest;
import com.example.java_clojure_performance_analyzer.models.response.CompoundInterestResponse;

public interface CompoundInterestService {
    CompoundInterestResponse calculateCompoundInterest(CompoundInterestRequest request);
}
