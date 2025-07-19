package com.example.java_clojure_performance_analyzer.models.response;

public class YearlyInvestmentSummary {
    private Integer year;
    private Double startBalance;
    private Double endBalance;
    private Double interestEarned;

    public YearlyInvestmentSummary(){}

    public YearlyInvestmentSummary(Integer year, Double startBalance, Double endBalance, Double interestEarned){
        this.year = year;
        this.startBalance = startBalance;
        this.endBalance = endBalance;
        this.interestEarned = interestEarned;
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
}
