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
    
    private static final int DECIMAL_PRECISION = 3;

    private double roundTo3Decimals(double value) {
        return BigDecimal.valueOf(value)
                .setScale(DECIMAL_PRECISION, RoundingMode.HALF_UP)
                .doubleValue();
    }

    private double calculateYearlyCompoundInterest(double principal, double annualRate) {
        if (principal < 0 || annualRate < 0) {
            throw new IllegalArgumentException("Principal and annual rate must be non-negative");
        }
        
        double rate = annualRate / 100.0;
        return roundTo3Decimals(principal * (1.0 + rate));
    }
    
    private YearlyBalanceDTO createYearlyBalance(int year, double startingBalance, double endingBalance) {
        double interestEarned = roundTo3Decimals(endingBalance - startingBalance);
        return new YearlyBalanceDTO(year, startingBalance, interestEarned, 0.0, endingBalance);
    }

    @Override
    public CompoundInterestResponseDTO calculateCompoundInterest(CompoundInterestRequestDTO request) {        
        final double initialAmount = request.getInitialAmount();
        final double annualRate = request.getAnnualInterestRate();
        final int years = request.getYears();

        final List<YearlyBalanceDTO> yearlyDetails = new ArrayList<>(years);
        double currentBalance = initialAmount;

        for (int year = 1; year <= years; year++) {
            double newBalance = calculateYearlyCompoundInterest(currentBalance, annualRate);
            
            YearlyBalanceDTO yearDetail = createYearlyBalance(year, currentBalance, newBalance);
            yearlyDetails.add(yearDetail);

            currentBalance = newBalance;
        }

        double totalInterest = roundTo3Decimals(currentBalance - initialAmount);
        CalculationDTO summary = new CalculationDTO(initialAmount, 0.0, totalInterest, currentBalance);

        return new CompoundInterestResponseDTO(summary, yearlyDetails);
    }
}