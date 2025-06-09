;; Defining Namespace
(ns com.example.financial-calculator-fp.service.compound-interest-service
  "Service for compound interest calculations using functional programing principles"
  (:import
   (com.example.financial_calculator_fp.services CompoundInterestService)
   (com.example.financial_calculator_fp.models.request CompoundInterestRequestDTO)
   (com.example.financial_calculator_fp.models.response CalculationDTO CompoundInterestResponseDTO YearlyBalanceDTO)))

(defn round-to-3-decimals 
  [^double value]
  (double (/ (Math/round (* value 1000.0)) 1000.0)))

(defn calculate-compound-interest-yearly                                              
  "Calculates compund Interest for a single year using the formula: P*(1+r)"  
  [^double initial-amount ^double annual-rate]                                
  (let [rate (/ annual-rate 100)                                              
        result (* initial-amount (+ 1 rate))]  
    (round-to-3-decimals result)))          

(defn calculate-compound-interest-years
  "Calculates compund Interest for multiple years, return yearly breakdown"
  [^double initial-amount ^double annual-rate ^long years]
  (loop [year 1
         current-balance initial-amount
         yearly-details  []] 
    (if (> year years)
      yearly-details  
      (let [new-balance     (calculate-compound-interest-yearly current-balance annual-rate)
            interest-earned (round-to-3-decimals (- new-balance current-balance))
            year-detail     {:year                year  
                             :starting-balance    current-balance
                             :interest-earned     interest-earned
                             :contributions-added 0.0
                             :ending-balance      new-balance}]
        (recur (inc year)
               new-balance
               (conj yearly-details year-detail))))))  ;; Adding the element to the collection in an immutable way / "conj" = "conjoins"

(defn create-yearly-dto
  "Transform a yearly detail map to java DTO"
  [detail]
  (YearlyBalanceDTO.
   (int (:year detail))
   (:starting-balance detail)
   (:interest-earned detail)
   (:contributions-added detail)
   (:ending-balance detail)))

(defn process-compound-interest-request
  "Process compound interest request and returns response DTO"
  [^CompoundInterestRequestDTO request]
  (let [initial-amount (.getInitialAmount request)
        annual-rate (.getAnnualInterestRate request)
        years (.getYears request)
        
        ;; Calculate yearly details 
        yearly-details (calculate-compound-interest-years initial-amount annual-rate years)
        
        ;; Extract summary information
        final-balance (:ending-balance (last yearly-details))
        total-interest (round-to-3-decimals (- final-balance initial-amount))
        
        ;; Transform to Java DTOs
        yearly-dtos (map create-yearly-dto yearly-details)
        calculation-dto (new CalculationDTO initial-amount 0.0 total-interest final-balance)]
    
    (new CompoundInterestResponseDTO calculation-dto yearly-dtos)))  

;; Function that will be called by the Java code in the configuration (external use -> ^:export)
(defn ^:export create-service 
  "Create a CompoundInterestService implementation using Clojure"
  []
  (reify CompoundInterestService  ;; Creation of an anonymous instance (object Java) that implements such interface
    (calculateCompoundInterest [_ request]  ;; Method name defined in Java interface / "request" is the CompoundInterestRequestDTO parameter 
      (process-compound-interest-request request))))