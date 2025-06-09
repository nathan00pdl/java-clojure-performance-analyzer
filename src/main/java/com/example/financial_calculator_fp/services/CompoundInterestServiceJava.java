package com.example.financial_calculator_fp.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.financial_calculator_fp.models.request.CompoundInterestRequestDTO;
import com.example.financial_calculator_fp.models.response.CalculationDTO;
import com.example.financial_calculator_fp.models.response.CompoundInterestResponseDTO;
import com.example.financial_calculator_fp.models.response.YearlyBalanceDTO;


@Service("javaImplementation")
public class CompoundInterestServiceJava implements CompoundInterestService {

    private double roundTo3Decimals(double value) {
        return BigDecimal.valueOf(value)
                .setScale(3, RoundingMode.HALF_UP)
                .doubleValue();
    }

    private double calculateYearlyInterest(double initialAmount, double annualRate) {
        double rate = annualRate / 100; 
        return roundTo3Decimals(initialAmount * (1.0 + rate));
    }

    private YearlyBalanceDTO createYearlyBalance(int year, double startingBalance, double endingBalance) {
        double interestEarned = roundTo3Decimals(endingBalance - startingBalance);
        return new YearlyBalanceDTO(year, startingBalance, endingBalance, 0.0, interestEarned);
    }

    @Override
    public CompoundInterestResponseDTO calculateCompoundInterest(CompoundInterestRequestDTO request) {
        final double initialAmount = request.getInitialAmount();
        final double annualRate = request.getAnnualInterestRate();
        final int years = request.getYears();

        final List<YearlyBalanceDTO> yearlyDetails = new ArrayList<>(years);
        double currentBalance = initialAmount;

        for(int year = 1; year <= years; year++) {
            double newBalance = calculateYearlyInterest(currentBalance, annualRate);

            YearlyBalanceDTO yearDetail = createYearlyBalance(year, newBalance, newBalance);
            yearlyDetails.add(yearDetail);

            currentBalance = newBalance;
        }

        double totalInterest = roundTo3Decimals(currentBalance - initialAmount);
        CalculationDTO summary = new CalculationDTO(initialAmount, 0.0, totalInterest, currentBalance);

        return new CompoundInterestResponseDTO(summary, yearlyDetails);
    }
}
