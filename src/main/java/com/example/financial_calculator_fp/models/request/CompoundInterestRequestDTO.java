package com.example.financial_calculator_fp.models.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class CompoundInterestRequestDTO {
    @NotNull(message = "O valor inicial não pode ser nulo")
    @Min(value = 0, message = "O valor inicial deve ser maior ou igual a zero")
    private Double initialAmount;

    @NotNull(message = "A taxa de juros anual não pode ser nula")
    @Positive(message = "A taxa de juros anual deve ser maior que zero")
    private Double annualInterestRate;

    @NotNull(message = "O número de anos não pode ser nulo")
    @Positive(message = "O número de anos deve ser maior que zero")
    private Integer years;

    @Valid
    private AdditionalContributionDTO additionalContributionDTO;

    public CompoundInterestRequestDTO() {
    }

    public CompoundInterestRequestDTO(Double initialAmount, Double annualInterestRate, Integer years,
            AdditionalContributionDTO additionalContributionDTO) {
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

    public AdditionalContributionDTO getAdditionalContributionDTO() {
        return additionalContributionDTO;
    }

    public void setAdditionalContributionDTO(AdditionalContributionDTO additionalContributionDTO) {
        this.additionalContributionDTO = additionalContributionDTO;
    }
}
