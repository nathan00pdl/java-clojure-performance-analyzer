package com.example.java_clojure_performance_analyzer.models.response;

public class InvestmentSummary {
    private Double initialInvestment;
    private Double finalBalance;
    private Double totalInterestEarned;

    public InvestmentSummary(){}

    public InvestmentSummary(Double initialInvestment, Double finalBalance, Double totalInterestEarned) {
        this.initialInvestment = initialInvestment;
        this.finalBalance = finalBalance;
        this.totalInterestEarned = totalInterestEarned;
    }

    public Double getInitialInvestment() {
        return initialInvestment;
    }

    public void setInitialInvestment(Double initialInvestment) {
        this.initialInvestment = initialInvestment;
    }

    public Double getFinalBalance() {
        return finalBalance;
    }

    public void setFinalBalance(Double finalBalance) {
        this.finalBalance = finalBalance;
    }

    public Double getTotalInterestEarned() {
        return totalInterestEarned;
    }

    public void setTotalInterestEarned(Double totalInterestEarned) {
        this.totalInterestEarned = totalInterestEarned;
    }
}
