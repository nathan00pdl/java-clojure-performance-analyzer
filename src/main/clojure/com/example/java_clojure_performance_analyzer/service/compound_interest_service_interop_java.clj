(ns com.example.java-clojure-performance-analyzer.service.compound-interest-service-interop-java
  (:import
   (com.example.java_clojure_performance_analyzer.services CompoundInterestService)
   (com.example.java_clojure_performance_analyzer.models.request CompoundInterestRequest)
   (com.example.java_clojure_performance_analyzer.models.response CompoundInterestResponse InvestmentSummary YearlyInvestmentSummary)
   (java.util ArrayList)))

(defn calculate-compound-interest
  [^CompoundInterestRequest request]
  
  ;; 1. Coercion to Java primitives to avoid Boxing overhead
  (let [initial-amount (double (.getInitialAmount request))  
        rate           (double (.getAnnualInterestRate request))
        years          (int (.getYears request))

        compound-factor (+ 1.0 (/ rate 100.0))

        ;; 2. Type Hint (^ArrayList) to avoid Reflection and pre-allocate a mutable list
        ^ArrayList yearly-details-list (ArrayList. years) 

        final-amount
        (loop [year           1
               current-amount initial-amount
               ]

          (if (> year years)
            current-amount

            (let [start-balance   current-amount
                  end-balance     (* current-amount compound-factor)
                  interest-earned (- end-balance start-balance)]

              ;; 3. Direct mutation of the ArrayList, avoiding the allocation of new objects (GC relief)
              (.add yearly-details-list 
                    (YearlyInvestmentSummary.
                     (int year)
                     start-balance
                     end-balance
                     interest-earned))

              ;; 4. Native JVM increment: bypasses Clojure's overflow safety checks for maximum CPU performance
              (recur (unchecked-inc year) 
                     end-balance)
              )
            )
          )

        total-interest-earned (- final-amount initial-amount)
        summary (InvestmentSummary. initial-amount final-amount total-interest-earned)]

    (CompoundInterestResponse. summary yearly-details-list))
    )

(defn ^:export create-service
  []
  (reify CompoundInterestService
    (calculateCompoundInterest [_ request]
      (calculate-compound-interest request)
      )
    )
  )