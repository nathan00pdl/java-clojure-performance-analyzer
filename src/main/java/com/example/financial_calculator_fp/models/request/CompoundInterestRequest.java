package com.example.financial_calculator_fp.models.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class CompoundInterestRequest {
    @NotNull(message = "Initial Value Cannot Be Null")
    @Min(value = 0, message = "The Initial Value Must Be Greater Than Or Equal To Zero")
    private Double initialAmount;

    @NotNull(message = "The Annual Interest Rate Cannot Be Zero")
    @Positive(message = "The Annual Interest Rate Must Be Greater Than Zero")
    private Double annualInterestRate;

    @NotNull(message = "The Number Of Years Cannot Be Zero")
    @Positive(message = "The Number Of Years Must Be Greater Than Zero")
    private Integer years;

    @Valid
    private AdditionalContribution additionalContributionDTO;

    public CompoundInterestRequest() {
    }

    public CompoundInterestRequest(Double initialAmount, Double annualInterestRate, Integer years,
            AdditionalContribution additionalContributionDTO) {
        this.initialAmount = initialAmount;
        this.annualInterestRate = annualInterestRate;
        this.years = years;
        this.additionalContributionDTO = additionalContributionDTO;
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

    public AdditionalContribution getAdditionalContributionDTO() {
        return additionalContributionDTO;
    }

    public void setAdditionalContributionDTO(AdditionalContribution additionalContributionDTO) {
        this.additionalContributionDTO = additionalContributionDTO;
    }
}
