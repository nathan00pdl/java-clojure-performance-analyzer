;; Defining Namespace
(ns com.example.financial-calculator-fp.service.compound-interest-service
  (:import
   (com.example.financial_calculator_fp.services CompoundInterestService)
   (com.example.financial_calculator_fp.models.request CompoundInterestRequestDTO)
   (com.example.financial_calculator_fp.models.response CalculationDTO CompoundInterestResponseDTO YearlyBalanceDTO)))

(defn round-to-3-decimals
  [^double value]
  (double (/ (Math/round (* value 1000.0)) 1000.0)))

(defn calculate-compound-interest-yearly
  [^double initial-amount ^double annual-rate]
  (let [rate (/ annual-rate 100)
        result (* initial-amount (+ 1 rate))]
    (round-to-3-decimals result)))

(defn calculate-compound-interest-years
  [^double initial-amount ^double annual-rate ^long years]
  (loop [year 1
         current-balance initial-amount
         yearly-details  []]
    (if (> year years)
      yearly-details  ;"Return yearly-details if true"
      (let [new-balance     (calculate-compound-interest-yearly current-balance annual-rate)
            interest-earned (round-to-3-decimals (- new-balance current-balance))
            year-detail     {:year                year  ;year-detils represents a key/value map
                             :starting-balance    current-balance
                             :interest-earned     interest-earned
                             :contributions-added 0.0
                             :ending-balance      new-balance}]
        (recur (inc year)
               new-balance
               (conj yearly-details year-detail))))))  ;Adding the element to the collection in an immutable way / "conj" = "conjoins"

(defn process-compound-interest-request
  [^CompoundInterestRequestDTO request]
  (let [;Extraction of request data
        initial-amount (.getInitialAmount request)  ; Java method call
        annual-rate (.getAnnualInterestRate request)
        years (.getYears request)

        yearly-details (calculate-compound-interest-years initial-amount annual-rate years)  ;Return an array of maps with details for each year

        final-balance (:ending-balance (last yearly-details)) ;Get the last element of the array


        total-interest (round-to-3-decimals (- final-balance initial-amount))

        ;Creating java objects for response (yearly-dtos and calculation-dto)
        yearly-dtos (map (fn [detail]  ;Will return a new collection with the results 
                           (new YearlyBalanceDTO  ;Creating a new instance of this class
                                (int (:year detail))
                                (:starting-balance detail)
                                (:interest-earned detail)
                                (:contributions-added detail)
                                (:ending-balance detail)))
                         yearly-details)
        calculation-dto (new CalculationDTO
                             initial-amount
                             0.0
                             total-interest
                             final-balance)]

    (new CompoundInterestResponseDTO calculation-dto yearly-dtos)))  ; Return response object

;Function that will be called by the Java code in the configuration (external use -> ^:export)
(defn ^:export create-service
  []
  (reify CompoundInterestService  ; Creation of an anonymous instance (object Java) that implements such interface
    (calculateCompoundInterest [_ request]  ; Method name defined in Java interface / "request" is the CompoundInterestRequestDTO parameter 
      (process-compound-interest-request request))))