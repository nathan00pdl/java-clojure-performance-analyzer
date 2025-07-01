package com.example.financial_calculator_fp.models.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class AdditionalContribution {
    @NotNull(message = "The Value Of The Additional Contribution Cannot Be Zero")
    @Min(value = 0, message = "The Amount Of The Additional Contribution Must Be Greater Than Or Equal To Zero")
    private Double amount;

    @NotNull(message = "The Frequency Of The Additional Contribution Cannot Be Zero")
    private ContributionFrequency frequency;

    public AdditionalContribution() {
    }

    public AdditionalContribution(Double amount, ContributionFrequency frequency) {
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
