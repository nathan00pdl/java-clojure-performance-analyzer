package com.example.financial_calculator_fp.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.financial_calculator_fp.models.request.CompoundInterestRequest;
import com.example.financial_calculator_fp.models.response.InvestmentSummary;
import com.example.financial_calculator_fp.models.response.CompoundInterestResponse;
import com.example.financial_calculator_fp.models.response.YearlyInvestmentSummary;

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
    
    private YearlyInvestmentSummary createYearlyInvestmentSummary(int year, double startBalance, double endBalance) {
        double interestEarned = round(endBalance - startBalance);
        return new YearlyInvestmentSummary(year, startBalance, endBalance, interestEarned, ADDITIONAL_CONTRIBUTION);
    }

    @Override
    public CompoundInterestResponse calculateCompoundInterest(CompoundInterestRequest request) {        
        final double initialAmount = request.getInitialAmount();
        final double rate = request.getAnnualInterestRate();
        final int years = request.getYears();

        double currentAmount = initialAmount;
        List<YearlyInvestmentSummary> yearlyDetailsList = new ArrayList<>(years);

        for (int year = 1; year <= years; year++) {
            double newAmount = applyCompoundInterest(currentAmount, rate);
            yearlyDetailsList.add(createYearlyInvestmentSummary(year, currentAmount, newAmount));
            currentAmount = newAmount;
        }

        double totalInterestEarned = round(currentAmount - request.getInitialAmount());
        InvestmentSummary summaryResults = new InvestmentSummary(request.getInitialAmount(), currentAmount, totalInterestEarned, ADDITIONAL_CONTRIBUTION);

        return new CompoundInterestResponse(summaryResults, yearlyDetailsList);
    }
}