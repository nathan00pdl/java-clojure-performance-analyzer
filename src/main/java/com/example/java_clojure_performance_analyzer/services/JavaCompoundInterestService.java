package com.example.java_clojure_performance_analyzer.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.java_clojure_performance_analyzer.models.request.CompoundInterestRequest;
import com.example.java_clojure_performance_analyzer.models.response.CompoundInterestResponse;
import com.example.java_clojure_performance_analyzer.models.response.InvestmentSummary;
import com.example.java_clojure_performance_analyzer.models.response.YearlyInvestmentSummary;

@Service("javaImplementation")
public class JavaCompoundInterestService implements CompoundInterestService {
    @Override
    public CompoundInterestResponse calculateCompoundInterest(CompoundInterestRequest request) {
        final double initialAmount = request.getInitialAmount();
        final double rate = request.getAnnualInterestRate();
        final int years = request.getYears();

        final double compoundFactor = (1.0 + rate / 100.0);

        final List<YearlyInvestmentSummary> yearlyDetailsList = new ArrayList<>(years);

        double currentAmount = initialAmount;

        for (int year = 1; year <= years; year++) {
            final double startBalance = currentAmount;
            final double endBalance = currentAmount * compoundFactor;
            final double interestEarned = endBalance - startBalance;

            yearlyDetailsList.add(new YearlyInvestmentSummary(year, startBalance, endBalance, interestEarned));

            currentAmount = endBalance;
        }

        final double totalInterestEarned = currentAmount - initialAmount;
        InvestmentSummary summary = new InvestmentSummary(initialAmount, currentAmount, totalInterestEarned);

        return new CompoundInterestResponse(summary, yearlyDetailsList);
    }
}