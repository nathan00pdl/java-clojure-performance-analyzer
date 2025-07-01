(ns com.example.financial-calculator-fp.service.compound-interest-service
  (:import
   (com.example.financial_calculator_fp.services CompoundInterestService)
   (com.example.financial_calculator_fp.models.request CompoundInterestRequest)
   (com.example.financial_calculator_fp.models.response CompoundInterestResponse InvestmentSummary YearlyInvestmentSummary)
   (com.example.financial_calculator_fp.exceptions ValidationException)))

(def ^:private ADDITIONAL_CONTRIBUTION 0.0)
(def ^:private MAX_YEARS 200)

(defn round
  [^double value]
  (double (/ (Math/round (* value 1000.0)) 1000.0)))

(defn validate-business-rules
  [^CompoundInterestRequest request]
  (let [years (.getYears request)]
    (when (> years MAX_YEARS)
      (throw (ValidationException. "years" "The Maximum Period Allowed is 200 years")))))

(defn calculate-compound-interest-yearly
  [^double current-amount ^double annual-rate]
  (let [rate (/ annual-rate 100)
        result (* current-amount (+ 1 rate))]
    (round result)))

(defn calculate-compound-interest-years
  [^double initial-amount ^double annual-rate ^long years]
  (loop [year 1
         current-amount initial-amount
         yearly-details-list  []]
    (if (> year years)
      yearly-details-list
      (let [new-amount      (calculate-compound-interest-yearly current-amount annual-rate)
            interest-earned (round (- new-amount current-amount))
            year-detail     {:year                         year
                             :start-balance               current-amount
                             :end-balance                 new-amount
                             :interest-earned             interest-earned
                             :additional-contributions    ADDITIONAL_CONTRIBUTION}]
        (recur (inc year)
               new-amount
               (conj yearly-details-list year-detail))))))

(defn process-compound-interest-request
  [^CompoundInterestRequest request]
  (validate-business-rules request)
  (let [
        initial-amount (.getInitialAmount request)
        rate (.getAnnualInterestRate request)
        years (.getYears request)

        yearly-details-list (calculate-compound-interest-years initial-amount rate years)
        current-amount (:end-balance (last yearly-details-list))
        total-interest-earned (round (- current-amount initial-amount))

        yearly-summary-results (map (fn [detail]
                           (new YearlyInvestmentSummary
                                (int (:year detail))               
                                (:start-balance detail)            
                                (:end-balance detail)              
                                (:interest-earned detail)          
                                (:additional-contributions detail))) 
                         yearly-details-list)

        summary-results (new InvestmentSummary
                             initial-amount
                             current-amount
                             total-interest-earned
                             ADDITIONAL_CONTRIBUTION)
        ]

    (new CompoundInterestResponse summary-results yearly-summary-results)))

(defn ^:export create-service
  []
  (reify CompoundInterestService
    (calculateCompoundInterest [_ request]
      (process-compound-interest-request request))))