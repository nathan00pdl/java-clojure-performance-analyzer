(ns com.example.java-clojure-performance-analyzer.service.compound-interest-service-optimized
  (:import
   (com.example.java_clojure_performance_analyzer.services CompoundInterestService)
   (com.example.java_clojure_performance_analyzer.models.request CompoundInterestRequest)
   (com.example.java_clojure_performance_analyzer.models.response CompoundInterestResponse InvestmentSummary YearlyInvestmentSummary)
   (java.math BigDecimal RoundingMode)))

(set! *warn-on-reflection* true)

(defn round-value
  [^double value]
  (let [^BigDecimal bd (BigDecimal/valueOf value)]
    (.doubleValue (.setScale bd 3 RoundingMode/HALF_UP))))

(defn apply-compound-interest
  [^double current-amount, ^double annual-rate]
  (let [rate (/ annual-rate 100.0)
        result (* current-amount (+ 1.0 rate))]
    (round-value result)))

(defn calculate-compound-interest-years
  [^double initial-amount, ^double annual-rate, ^long years]
  (loop [year 1
         current-amount initial-amount 
         yearly-details-list []
         ]
    (if (> year years)
      yearly-details-list
      (let [new-amount (apply-compound-interest current-amount annual-rate)
            interest-earned (round-value (- new-amount current-amount))
            year-detail {:year            year
                         :start-balance   current-amount
                         :end-balance     new-amount
                         :interest-earned interest-earned}]
        (recur (inc year)
               new-amount
               (conj yearly-details-list year-detail))))))

(defn calculate-compound-interest-request
  [^CompoundInterestRequest request]
  (let [initial-amount (.getInitialAmount request)
        rate           (.getAnnualInterestRate request)
        years            (.getYears request)

        yearly-details-list    (calculate-compound-interest-years initial-amount rate years)
        last-detail (last yearly-details-list)
        current-amount   (:end-balance last-detail)
        total-interest-earned (round-value (- current-amount initial-amount))

        yearly-summary-results (map (fn [detail]
                                      (new YearlyInvestmentSummary
                                           (int (:year detail))
                                           (double (:start-balance detail))
                                           (double (:end-balance detail))
                                           (double (:interest-earned detail))))
                                    yearly-details-list)

        summary-results (new InvestmentSummary
                             initial-amount
                             current-amount
                             total-interest-earned)]
    (new CompoundInterestResponse summary-results (vec yearly-summary-results))))

(defn ^:export create-service
  []
  (reify CompoundInterestService
    (calculateCompoundInterest [_ request]
      (calculate-compound-interest-request request))))