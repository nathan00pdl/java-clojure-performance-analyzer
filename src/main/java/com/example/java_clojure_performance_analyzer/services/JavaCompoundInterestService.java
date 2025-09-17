package com.example.java_clojure_performance_analyzer.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.java_clojure_performance_analyzer.models.request.CompoundInterestRequest;
import com.example.java_clojure_performance_analyzer.models.response.CompoundInterestResponse;
import com.example.java_clojure_performance_analyzer.models.response.InvestmentSummary;
import com.example.java_clojure_performance_analyzer.models.response.YearlyInvestmentSummary;

@Service("javaImplementation")
public class JavaCompoundInterestService implements CompoundInterestService {
    private static final int DECIMAL_PRECISION = 3;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    private double round(double value) {
        return BigDecimal.valueOf(value).setScale(DECIMAL_PRECISION, ROUNDING_MODE).doubleValue();
    }

    private double applyCompoundInterest(double amount, double rate) {
        return round(amount * (1.0 + rate / 100.0));
    }

    @Override
    public CompoundInterestResponse calculateCompoundInterest(CompoundInterestRequest request) {        
        final double initialAmount = request.getInitialAmount();
        final double rate = request.getAnnualInterestRate();
        final int years = request.getYears();

        final List<YearlyInvestmentSummary> yearlyDetailsList = new ArrayList<>(years);
        
        double currentAmount = initialAmount;

        for (int year = 1; year <= years; year++) {
            final double startBalance = currentAmount;
            final double endBalance = applyCompoundInterest(currentAmount, rate);
            final double interestEarned = round(endBalance - startBalance);
            
            yearlyDetailsList.add( new YearlyInvestmentSummary(year, startBalance, endBalance, interestEarned));
            
            currentAmount = endBalance;
        }

        final double totalInterestEarned = round(currentAmount - initialAmount);
        InvestmentSummary summary = new InvestmentSummary(initialAmount, currentAmount, totalInterestEarned);

        return new CompoundInterestResponse(summary, yearlyDetailsList);
    }
}