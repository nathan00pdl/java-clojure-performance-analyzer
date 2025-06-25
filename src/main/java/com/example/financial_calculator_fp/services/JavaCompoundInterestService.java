package com.example.financial_calculator_fp.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.financial_calculator_fp.models.request.CompoundInterestRequestDTO;
import com.example.financial_calculator_fp.models.response.InvestmentSummaryDTO;
import com.example.financial_calculator_fp.models.response.CompoundInterestResponseDTO;
import com.example.financial_calculator_fp.models.response.YearlyInvestmentSummaryDTO;

@Service("javaImplementation")
public class JavaCompoundInterestService implements CompoundInterestService {
    
    private static final int DECIMAL_PRECISION = 3;
    private static final double ADDITIONAL_CONTRIBUTION = 0.0;

    private double round(double value) {
        return BigDecimal.valueOf(value)
                .setScale(DECIMAL_PRECISION, RoundingMode.HALF_UP)
                .doubleValue();
    }

    private double applyCompoundInterest(double amount, double rate) {
        return round(amount * (1 + rate / 100.0));
    }
    
    private YearlyInvestmentSummaryDTO createYearlyInvestmentSummaryDTO(int year, double startBalance, double endBalance) {
        double interestEarned = round(endBalance - startBalance);
        return new YearlyInvestmentSummaryDTO(year, startBalance, endBalance, interestEarned, ADDITIONAL_CONTRIBUTION);
    }

    @Override
    public CompoundInterestResponseDTO calculateCompoundInterest(CompoundInterestRequestDTO request) {        
        final double initialAmount = request.getInitialAmount();
        final double rate = request.getAnnualInterestRate();
        final int years = request.getYears();

        double currentAmount = initialAmount;
        List<YearlyInvestmentSummaryDTO> yearlyDetailsList = new ArrayList<>(years);

        for (int year = 1; year <= years; year++) {
            double newAmount = applyCompoundInterest(currentAmount, rate);
            yearlyDetailsList.add(createYearlyInvestmentSummaryDTO(year, currentAmount, newAmount));
            currentAmount = newAmount;
        }

        double totalInterestEarned = round(currentAmount - request.getInitialAmount());
        InvestmentSummaryDTO summaryResults = new InvestmentSummaryDTO(request.getInitialAmount(), currentAmount, totalInterestEarned, ADDITIONAL_CONTRIBUTION);

        return new CompoundInterestResponseDTO(summaryResults, yearlyDetailsList);
    }
}