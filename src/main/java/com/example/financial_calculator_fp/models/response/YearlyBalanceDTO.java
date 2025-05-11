package com.example.financial_calculator_fp.models.response;

public class YearlyBalanceDTO {
    private Integer year;
    private Double startingBalance;
    private Double interestEarned;
    private Double contributionsAdded;
    private Double endingBalance;

    public YearlyBalanceDTO(){}

    public YearlyBalanceDTO(Integer year, Double startingBalance, Double interestEarned, Double contributionsAdded, Double endingBalance){
        this.year = year;
        this.startingBalance = startingBalance;
        this.interestEarned = interestEarned;
        this.contributionsAdded = contributionsAdded;
        this.endingBalance = endingBalance;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Double getStartingBalance() {
        return startingBalance;
    }

    public void setStartingBalance(Double startingBalance) {
        this.startingBalance = startingBalance;
    }

    public Double getInterestEarned() {
        return interestEarned;
    }

    public void setInterestEarned(Double interestEarned) {
        this.interestEarned = interestEarned;
    }

    public Double getContributionsAdded() {
        return contributionsAdded;
    }

    public void setContributionsAdded(Double contributionsAdded) {
        this.contributionsAdded = contributionsAdded;
    }

    public Double getEndingBalance() {
        return endingBalance;
    }

    public void setEndingBalance(Double endingBalance) {
        this.endingBalance = endingBalance;
    }

    
}
