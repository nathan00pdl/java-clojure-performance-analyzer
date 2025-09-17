(ns com.example.java-clojure-performance-analyzer.service.compound-interest-service-idiomatic
  (:import
   (com.example.java_clojure_performance_analyzer.services CompoundInterestService)
   (com.example.java_clojure_performance_analyzer.models.request CompoundInterestRequest)
   (com.example.java_clojure_performance_analyzer.models.response CompoundInterestResponse InvestmentSummary YearlyInvestmentSummary)
   (java.math BigDecimal RoundingMode)))

(defn round
  [value]
  (let [^BigDecimal bd (BigDecimal/valueOf value)]
    (.doubleValue (.setScale bd 3 RoundingMode/HALF_UP))))

(defn calculate-compound-interest-yearly
  [current-amount annual-rate]
  (let [rate (/ annual-rate 100.0) 
        result (* current-amount (+ 1.0 rate))] 
    (round result)))

(defn calculate-compound-interest-years
  [initial-amount annual-rate years]
  (loop [year 1 
         current-amount initial-amount
         yearly-details-list  []]
    (if (> year years)
      yearly-details-list
      (let [new-amount (calculate-compound-interest-yearly current-amount annual-rate)
            interest-earned (- new-amount current-amount)
            year-detail     {:year                year
                             :start-balance       current-amount
                             :end-balance         new-amount
                             :interest-earned     interest-earned}
            ]
        (recur (inc year)
               new-amount
               (conj yearly-details-list year-detail))))))

(defn process-compound-interest-request
  [^CompoundInterestRequest request] 
  (let [initial-amount (double (.getInitialAmount request))
        rate (double (.getAnnualInterestRate request))
        years (int (.getYears request))

        yearly-details-list (calculate-compound-interest-years initial-amount rate years)
        current-amount (:end-balance (last yearly-details-list))
        total-interest-earned (- current-amount initial-amount)

        yearly-summary-results (map (fn [^clojure.lang.IPersistentMap detail]
                                      (new YearlyInvestmentSummary
                                           (int (:year detail))
                                           (double (:start-balance detail))
                                           (double (:end-balance detail))
                                           (double (:interest-earned detail))))
                                    yearly-details-list)

        summary-results (new InvestmentSummary
                             initial-amount
                             current-amount
                             total-interest-earned)
        ]
    (new CompoundInterestResponse summary-results yearly-summary-results)))

(defn ^:export create-service
  []
  (reify CompoundInterestService
    (calculateCompoundInterest [_ request]
      (process-compound-interest-request request))))