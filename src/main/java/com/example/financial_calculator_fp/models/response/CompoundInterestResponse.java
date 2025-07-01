package com.example.financial_calculator_fp.models.response;

import java.util.List;

public class CompoundInterestResponse {
    private  InvestmentSummary summaryResults;
    private List<YearlyInvestmentSummary> yearlyDetailsList;

    public CompoundInterestResponse(){}

    public CompoundInterestResponse(InvestmentSummary summaryResults, List<YearlyInvestmentSummary> yearlyDetailsList) {
        this.summaryResults = summaryResults;
        this.yearlyDetailsList = yearlyDetailsList;
    }

    public InvestmentSummary getSummaryResults() {
        return summaryResults;
    }

    public void setSummaryResults(InvestmentSummary summaryResults) {
        this.summaryResults = summaryResults;
    }

    public List<YearlyInvestmentSummary> getYearlyDetailsList() {
        return yearlyDetailsList;
    }

    public void setYearlyDetailsList(List<YearlyInvestmentSummary> yearlyDetailsList) {
        this.yearlyDetailsList = yearlyDetailsList;
    }
}
