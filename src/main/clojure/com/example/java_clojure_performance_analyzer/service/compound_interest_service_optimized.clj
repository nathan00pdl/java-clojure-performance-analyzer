(ns com.example.java-clojure-performance-analyzer.service.compound-interest-service-optimized
  (:import
   (com.example.java_clojure_performance_analyzer.services CompoundInterestService)
   (com.example.java_clojure_performance_analyzer.models.request CompoundInterestRequest)
   (com.example.java_clojure_performance_analyzer.models.response CompoundInterestResponse InvestmentSummary YearlyInvestmentSummary)
   (java.util ArrayList)))

(defn calculate-compound-interest
  [^CompoundInterestRequest request]
  (let [initial-amount (double (.getInitialAmount request))
        rate           (double (.getAnnualInterestRate request))
        years          (int (.getYears request))

        compound-factor (+ 1.0 (/ rate 100.0))

        ^ArrayList yearly-details-list (ArrayList. years)

        final-amount
        (loop [year           1
               current-amount initial-amount]

          (if (> year years)
            current-amount

            (let [start-balance   current-amount
                  end-balance     (* current-amount compound-factor)
                  interest-earned (- end-balance start-balance)]

              (.add yearly-details-list
                    (YearlyInvestmentSummary.
                     (int year)
                     start-balance
                     end-balance
                     interest-earned))

              (recur (unchecked-inc year)
                     end-balance))))

        total-interest-earned (- final-amount initial-amount)
        summary (InvestmentSummary. initial-amount final-amount total-interest-earned)]

    (CompoundInterestResponse. summary yearly-details-list)))

(defn ^:export create-service
  []
  (reify CompoundInterestService
    (calculateCompoundInterest [_ request]
      (calculate-compound-interest request))))