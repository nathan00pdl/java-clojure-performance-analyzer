package com.example.financial_calculator_fp.models.response;

public class CalculationDTO {
    private Double initialInvestment;
    private Double totalContributions;
    private Double totalInterestEarned;
    private Double finalBalance;

    public CalculationDTO(){}

    public CalculationDTO(Double initialInvestment, Double totalContributions, Double totalInterestEarned, Double finalBalance) {
        this.initialInvestment = initialInvestment;
        this.totalContributions = totalContributions;
        this.totalInterestEarned = totalInterestEarned;
        this.finalBalance = finalBalance;
    }

    public Double getInitialInvestment() {
        return initialInvestment;
    }

    public void setInitialInvestment(Double initialInvestment) {
        this.initialInvestment = initialInvestment;
    }

    public Double getTotalContributions() {
        return totalContributions;
    }

    public void setTotalContributions(Double totalContributions) {
        this.totalContributions = totalContributions;
    }

    public Double getTotalInterestEarned() {
        return totalInterestEarned;
    }

    public void setTotalInterestEarned(Double totalInterestEarned) {
        this.totalInterestEarned = totalInterestEarned;
    }

    public Double getFinalBalance() {
        return finalBalance;
    }

    public void setFinalBalance(Double finalBalance) {
        this.finalBalance = finalBalance;
    }
    
    
}
