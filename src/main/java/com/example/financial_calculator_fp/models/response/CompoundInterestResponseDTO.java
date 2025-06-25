package com.example.financial_calculator_fp.models.response;

import java.util.List;

public class CompoundInterestResponseDTO {
    private  InvestmentSummaryDTO summaryResults;
    private List<YearlyInvestmentSummaryDTO> yearlyDetailsList;

    public CompoundInterestResponseDTO(){}

    public CompoundInterestResponseDTO(InvestmentSummaryDTO summaryResults, List<YearlyInvestmentSummaryDTO> yearlyDetailsList) {
        this.summaryResults = summaryResults;
        this.yearlyDetailsList = yearlyDetailsList;
    }

    public InvestmentSummaryDTO getSummaryResults() {
        return summaryResults;
    }

    public void setSummaryResults(InvestmentSummaryDTO summaryResults) {
        this.summaryResults = summaryResults;
    }

    public List<YearlyInvestmentSummaryDTO> getYearlyDetailsList() {
        return yearlyDetailsList;
    }

    public void setYearlyDetailsList(List<YearlyInvestmentSummaryDTO> yearlyDetailsList) {
        this.yearlyDetailsList = yearlyDetailsList;
    }
}
