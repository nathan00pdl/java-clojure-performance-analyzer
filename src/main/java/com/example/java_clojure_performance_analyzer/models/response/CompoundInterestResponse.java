package com.example.java_clojure_performance_analyzer.models.response;

import java.util.List;

public class CompoundInterestResponse {
    private  InvestmentSummary summary;
    private List<YearlyInvestmentSummary> yearlyDetailsList;

    public CompoundInterestResponse(){}

    public CompoundInterestResponse(InvestmentSummary summary, List<YearlyInvestmentSummary> yearlyDetailsList) {
        this.summary = summary;
        this.yearlyDetailsList = yearlyDetailsList;
    }

    public InvestmentSummary getSummary() {
        return summary;
    }

    public void setSummary(InvestmentSummary summary) {
        this.summary = summary;
    }

    public List<YearlyInvestmentSummary> getYearlyDetailsList() {
        return yearlyDetailsList;
    }

    public void setYearlyDetailsList(List<YearlyInvestmentSummary> yearlyDetailsList) {
        this.yearlyDetailsList = yearlyDetailsList;
    }
}
