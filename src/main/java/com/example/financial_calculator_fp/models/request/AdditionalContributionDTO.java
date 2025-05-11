package com.example.financial_calculator_fp.models.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class AdditionalContributionDTO {
    @NotNull(message = "O valor da contribuição adicional não pode ser nulo")
    @Min(value = 0, message = "O valor da contribuição adicional deve ser maior ou igual a zero")
    private Double amount;

    @NotNull(message = "A frequência da contribuição adicional não pode ser nula")
    private ContributionFrequency frequency;

    public AdditionalContributionDTO() {
    }

    public AdditionalContributionDTO(Double amount, ContributionFrequency frequency) {
        this.amount = amount;
        this.frequency = frequency;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public ContributionFrequency getFrequency() {
        return frequency;
    }

    public void setFrequency(ContributionFrequency frequency) {
        this.frequency = frequency;
    }

}
