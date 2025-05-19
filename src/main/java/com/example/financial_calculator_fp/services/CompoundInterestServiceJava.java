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
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(3, RoundingMode.HALF_UP);

        return bd.doubleValue();
    }

    @Override
    public CompoundInterestResponseDTO calculateCompoundInterest(CompoundInterestRequestDTO request) {
        double initialAmount = request.getInitialAmount();
        double annualRate = request.getAnnualInterestRate();
        int years = request.getYears();

        List<YearlyBalanceDTO> yearlyDetails = new ArrayList<>();

        double currentBalance = initialAmount;

        for (int year = 1; year <= years; year++) {
            double newBalance = roundTo3Decimals(currentBalance * (1 + annualRate / 100));
            double interestEarned = roundTo3Decimals(newBalance - currentBalance);

            YearlyBalanceDTO yearDetail = new YearlyBalanceDTO(year, currentBalance, interestEarned, 0.0, newBalance);

            yearlyDetails.add(yearDetail);
            currentBalance = newBalance;
        }

        double totalInterest = roundTo3Decimals(currentBalance - initialAmount);
        CalculationDTO summary = new CalculationDTO(initialAmount, 0.0, totalInterest, currentBalance);

        return new CompoundInterestResponseDTO(summary, yearlyDetails);
    }

}
