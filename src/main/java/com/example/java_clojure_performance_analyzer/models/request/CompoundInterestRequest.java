package com.example.java_clojure_performance_analyzer.models.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public class CompoundInterestRequest {
    @NotNull(message = "Initial Value Cannot Be Null")
    @PositiveOrZero(message = "The Initial Value Must Be Greater Than Or Equal To Zero")
    private Double initialAmount;

    @NotNull(message = "The Annual Interest Rate Cannot Be Zero")
    @Positive(message = "The Annual Interest Rate Must Be Greater Than Zero")
    private Double annualInterestRate;

    @NotNull(message = "The Number Of Years Cannot Be Zero")
    @Positive(message = "The Number Of Years Must Be Greater Than Zero")
    @Max(value = 200, message = "The Maximum Period Allowed Is 200 Years!")
    private Integer years;

    public CompoundInterestRequest() {
    }

    public CompoundInterestRequest(Double initialAmount, Double annualInterestRate, Integer years) {
        this.initialAmount = initialAmount;
        this.annualInterestRate = annualInterestRate;
        this.years = years;
    }

    public Double getInitialAmount() {
        return initialAmount;
    }

    public void setInitialAmount(Double initialAmount) {
        this.initialAmount = initialAmount;
    }

    public Double getAnnualInterestRate() {
        return annualInterestRate;
    }

    public void setAnnualInterestRate(Double annualInterestRate) {
        this.annualInterestRate = annualInterestRate;
    }

    public Integer getYears() {
        return years;
    }

    public void setYears(Integer years) {
        this.years = years;
    }
}
