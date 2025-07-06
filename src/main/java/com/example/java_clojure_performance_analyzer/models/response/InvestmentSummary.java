package com.example.java_clojure_performance_analyzer.models.response;

public class InvestmentSummary {
    private Double initialInvestment;
    private Double finalBalance;
    private Double totalInterestEarned;
    private Double totalAdditionalContributions;

    public InvestmentSummary(){}

    public InvestmentSummary(Double initialInvestment, Double finalBalance, Double totalInterestEarned, Double totalAdditionalContributions) {
        this.initialInvestment = initialInvestment;
        this.finalBalance = finalBalance;
        this.totalInterestEarned = totalInterestEarned;
        this.totalAdditionalContributions = totalAdditionalContributions;
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

    public Double getTotalAdditionalContributions() {
        return totalAdditionalContributions;
    }

    public void setTotalAdditionalContributions(Double totalAdditionalContributions) {
        this.totalAdditionalContributions = totalAdditionalContributions;
    }
}
