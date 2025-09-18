(ns com.example.java-clojure-performance-analyzer.service.compound-interest-service-optimized
  (:import
   (com.example.java_clojure_performance_analyzer.services CompoundInterestService)
   (com.example.java_clojure_performance_analyzer.models.request CompoundInterestRequest)
   (com.example.java_clojure_performance_analyzer.models.response CompoundInterestResponse InvestmentSummary YearlyInvestmentSummary)
   (java.math BigDecimal RoundingMode)))

(set! *warn-on-reflection* true)

(defn ^double round-value
  [^double value]
  (let [^BigDecimal bd (BigDecimal/valueOf value)]
    (.doubleValue (.setScale bd 3 RoundingMode/HALF_UP))))

(defn ^double apply-compound-interest
  [^double current-amount, ^double annual-rate]
  (let [rate (/ annual-rate 100.0)
        result (* current-amount (+ 1.0 rate))]
    (round-value result)))

(defn calculate-compound-interest-years
  [^double initial-amount, ^double annual-rate, ^long years]
  (loop [^long year 1
         ^double current-amount initial-amount
         yearly-details-list []]
    (if (> year years)
      yearly-details-list
      (let [^double new-amount (apply-compound-interest current-amount annual-rate)
            ^double interest-earned (round-value (- new-amount current-amount))
            year-detail {:year            year
                         :start-balance   current-amount
                         :end-balance     new-amount
                         :interest-earned interest-earned}]
        (recur (inc year)
               new-amount
               (conj yearly-details-list year-detail))))))

(defn ^CompoundInterestResponse calculate-compound-interest-request
  [^CompoundInterestRequest request]
  (let [^double initial-amount (.getInitialAmount request)
        ^double rate           (.getAnnualInterestRate request)
        ^long years            (.getYears request)

        yearly-details-list    (calculate-compound-interest-years initial-amount rate years)
        ^clojure.lang.IPersistentMap last-detail (last yearly-details-list)
        ^double current-amount   (:end-balance last-detail)
        ^double total-interest-earned (round-value (- current-amount initial-amount))

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
                             total-interest-earned)]
    (new CompoundInterestResponse summary-results (vec yearly-summary-results))))

(defn ^:export create-service
  []
  (reify CompoundInterestService
    (calculateCompoundInterest [_ request]
      (calculate-compound-interest-request request))))