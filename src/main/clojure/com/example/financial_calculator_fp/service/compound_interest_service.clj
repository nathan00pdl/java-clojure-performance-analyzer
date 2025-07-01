(ns com.example.financial-calculator-fp.service.compound-interest-service
  (:import
   (com.example.financial_calculator_fp.services CompoundInterestService)
   (com.example.financial_calculator_fp.models.request CompoundInterestRequestDTO)
   (com.example.financial_calculator_fp.models.response CompoundInterestResponseDTO InvestmentSummaryDTO YearlyInvestmentSummaryDTO)
   (com.example.financial_calculator_fp.exceptions ValidationException)))

(defn round
  [^double value]
  (double (/ (Math/round (* value 1000.0)) 1000.0)))

(defn validate-business-rules
  [^CompoundInterestRequestDTO request]
  (let [years (.getYears request)]
    (when (> years 200)
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
                             :additional-contributions    0.0}]
        (recur (inc year)
               new-amount
               (conj yearly-details-list year-detail))))))

(defn process-compound-interest-request
  [^CompoundInterestRequestDTO request]
  (validate-business-rules request)
  (let [
        initial-amount (.getInitialAmount request)
        rate (.getAnnualInterestRate request)
        years (.getYears request)

        yearly-details-list (calculate-compound-interest-years initial-amount rate years)
        final-balance (:end-balance (last yearly-details-list))
        total-interest-earned (round (- final-balance initial-amount))

        yearly-dtos (map (fn [detail]
                           (new YearlyInvestmentSummaryDTO
                                (int (:year detail))               
                                (:start-balance detail)            
                                (:end-balance detail)              
                                (:interest-earned detail)          
                                (:additional-contributions detail))) 
                         yearly-details-list)

        summary-results (new InvestmentSummaryDTO
                             initial-amount
                             final-balance
                             total-interest-earned
                             0.0)
        ]

    (new CompoundInterestResponseDTO summary-results yearly-dtos)))

(defn ^:export create-service
  []
  (reify CompoundInterestService
    (calculateCompoundInterest [_ request]
      (process-compound-interest-request request))))