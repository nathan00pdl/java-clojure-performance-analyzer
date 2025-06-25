package com.example.financial_calculator_fp.models.response;

public class InvestmentSummaryDTO {
    private Double initialInvestment;
    private Double finalBalance;
    private Double totalAdditionalContributions;
    private Double totalInterestEarned;

    public InvestmentSummaryDTO(){}

    public InvestmentSummaryDTO(Double initialInvestment, Double finalBalance, Double totalAdditionalContributions, Double totalInterestEarned) {
        this.initialInvestment = initialInvestment;
        this.totalAdditionalContributions = totalAdditionalContributions;
        this.totalInterestEarned = totalInterestEarned;
        this.finalBalance = finalBalance;
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

    public Double getTotalAdditionalContributions() {
        return totalAdditionalContributions;
    }

    public void setTotalAdditionalContributions(Double totalAdditionalContributions) {
        this.totalAdditionalContributions = totalAdditionalContributions;
    }

    public Double getTotalInterestEarned() {
        return totalInterestEarned;
    }

    public void setTotalInterestEarned(Double totalInterestEarned) {
        this.totalInterestEarned = totalInterestEarned;
    }
}
