;; Defining Namespace
(ns com.example.financial-calculator-fp.service.compound-interest-service
  (:import
   (com.example.financial_calculator_fp.services CompoundInterestService)
   (com.example.financial_calculator_fp.models.request CompoundInterestRequestDTO)
   (com.example.financial_calculator_fp.models.response CompoundInterestResponseDTO InvestmentSummaryDTO YearlyInvestmentSummaryDTO)))

(defn round
  [^double value]
  (double (/ (Math/round (* value 1000.0)) 1000.0)))

(defn calculate-compound-interest-yearly
  [^double initial-amount ^double annual-rate]
  (let [rate (/ annual-rate 100)
        result (* initial-amount (+ 1 rate))]
    (round result)))

(defn calculate-compound-interest-years
  [^double initial-amount ^double annual-rate ^long years]
  (loop [year 1
         current-balance initial-amount
         yearly-details  []]
    (if (> year years)
      yearly-details  ;"Return yearly-details if true"
      (let [new-balance     (calculate-compound-interest-yearly current-balance annual-rate)
            interest-earned (round (- new-balance current-balance))
            year-detail     {:year                year  ;year-detils represents a key/value map
                             :start-balance       current-balance
                             :end-balance      new-balance 
                             :additional-contributions 0.0
                             :interest-earned     interest-earned}]
        (recur (inc year)
               new-balance
               (conj yearly-details year-detail))))))  ;Adding the element to the collection in an immutable way / "conj" = "conjoins"

(defn process-compound-interest-request
  [^CompoundInterestRequestDTO request]
  (let [
        initial-amount (.getInitialAmount request) 
        annual-rate (.getAnnualInterestRate request)
        years (.getYears request)

        yearly-details (calculate-compound-interest-years initial-amount annual-rate years)  ;Return an array of maps with details for each year 
        final-balance (:end-balance (last yearly-details)) 
        total-interest (round (- final-balance initial-amount))

        ;Creating java objects for response 
        yearly-dtos (map (fn [detail]  
                           (new YearlyInvestmentSummaryDTO  
                                (int (:year detail))
                                (:start-balance detail)
                                (:end-balance detail)
                                (:additional-contributions detail)
                                (:interest-earned detail)))
                         yearly-details)
        calculation-dto (new InvestmentSummaryDTO
                             initial-amount
                             0.0
                             total-interest
                             final-balance)]

    (new CompoundInterestResponseDTO calculation-dto yearly-dtos)))  

;Function that will be called by the Java code in the configuration (external use -> ^:export)
(defn ^:export create-service
  []
  (reify CompoundInterestService  ; Creation of an anonymous instance (object Java) that implements such interface
    (calculateCompoundInterest [_ request]  ; Method name defined in Java interface / "request" is the CompoundInterestRequestDTO parameter 
      (process-compound-interest-request request))))