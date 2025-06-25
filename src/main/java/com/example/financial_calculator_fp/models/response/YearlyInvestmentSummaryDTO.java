package com.example.financial_calculator_fp.models.response;

public class YearlyInvestmentSummaryDTO {
    private Integer year;
    private Double startBalance;
    private Double endBalance;
    private Double interestEarned;
    private Double additionalContribution;

    public YearlyInvestmentSummaryDTO(){}

    public YearlyInvestmentSummaryDTO(Integer year, Double startBalance, Double endBalance, Double interestEarned, Double additionalContribution){
        this.year = year;
        this.startBalance = startBalance;
        this.interestEarned = interestEarned;
        this.additionalContribution = additionalContribution;
        this.endBalance = endBalance;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Double getStartBalance() {
        return startBalance;
    }

    public void setStartBalance(Double startBalance) {
        this.startBalance = startBalance;
    }

    public Double getEndBalance() {
        return endBalance;
    }

    public void setEndBalance(Double endBalance) {
        this.endBalance = endBalance;
    }

    public Double getInterestEarned() {
        return interestEarned;
    }

    public void setInterestEarned(Double interestEarned) {
        this.interestEarned = interestEarned;
    }

    public Double getAdditionalContribution() {
        return additionalContribution;
    }

    public void setAdditionalContribution(Double additionalContribution) {
        this.additionalContribution = additionalContribution;
    }
}
