package com.example.financial_calculator_fp.models.response;

import java.util.List;

public class CompoundInterestResponseDTO {
    private  CalculationDTO summaryResults;
    private List<YearlyBalanceDTO> yearDetails;

    public CompoundInterestResponseDTO(){}

    public CompoundInterestResponseDTO(CalculationDTO summaryResults, List<YearlyBalanceDTO> yearDetails) {
        this.summaryResults = summaryResults;
        this.yearDetails = yearDetails;
    }

    public CalculationDTO getSummaryResults() {
        return summaryResults;
    }

    public void setSummaryResults(CalculationDTO summaryResults) {
        this.summaryResults = summaryResults;
    }

    public List<YearlyBalanceDTO> getYearDetails() {
        return yearDetails;
    }

    public void setYearDetails(List<YearlyBalanceDTO> yearDetails) {
        this.yearDetails = yearDetails;
    }
}
