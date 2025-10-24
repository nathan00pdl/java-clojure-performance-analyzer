(ns com.example.java-clojure-performance-analyzer.service.compound-interest-service-idiomatic
  (:import
   (com.example.java_clojure_performance_analyzer.services CompoundInterestService)
   (com.example.java_clojure_performance_analyzer.models.request CompoundInterestRequest)
   (com.example.java_clojure_performance_analyzer.models.response CompoundInterestResponse InvestmentSummary YearlyInvestmentSummary)))

(defn calculate-compound-interest
  [^CompoundInterestRequest request]
  (let [initial-amount (.getInitialAmount request)
        rate           (.getAnnualInterestRate request)
        years          (.getYears request)

        compound-factor (+ 1.0 (/ rate 100.0))

        yearly-details-list (loop [year                1
                                   current-amount      initial-amount
                                   yearly-details-list []
                                   ]

                              (if (> year years)
                                yearly-details-list

                                (let [start-balance   current-amount
                                      end-balance     (* current-amount compound-factor)
                                      interest-earned (- end-balance start-balance)
                                      ]

                                  (recur (inc year)
                                         end-balance
                                         (conj yearly-details-list
                                               (YearlyInvestmentSummary.
                                                (int year)
                                                (double start-balance)
                                                (double end-balance)
                                                (double interest-earned)
                                                )
                                               )
                                         )
                                  )
                                )
                              )

        final-amount          (if (empty? yearly-details-list)
                                initial-amount
                                (.getEndBalance ^YearlyInvestmentSummary (last yearly-details-list))
                                )
        
        total-interest-earned (- final-amount initial-amount)

        summary (InvestmentSummary. initial-amount final-amount total-interest-earned)
        ]

    (CompoundInterestResponse. summary yearly-details-list)
    )
    )

(defn ^:export create-service
  []
  (reify CompoundInterestService
    (calculateCompoundInterest [_ request]
      (calculate-compound-interest request)
      )
    )
  )