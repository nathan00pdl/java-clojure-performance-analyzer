"
Features:
 
-Calculation logic - calculate-interest-for-year
-Iteration for multiple years - calculate-interest-for-multiple-years
-Request / Response processing - process-request
-Integration with Java - create-service
"

"
current balance - saldo atual 
interest earned - juros ganho
"

;; Defining Namespace
(ns com.example.financial-calculator-fp.service.compound-interest-service
  (:import
   (com.example.financial_calculator_fp.services CompoundInterestService)
   (com.example.financial_calculator_fp.models.request CompoundInterestRequestDTO)
   (com.example.financial_calculator_fp.models.response CalculationDTO CompoundInterestResponseDTO YearlyBalanceDTO)))

(defn round-to-3-decimals [value]
  (double (/ (Math/round (* value 1000.0)) 1000.0)))

(defn calculate-interest-for-year              ;Name of function
  "Calculo de juros compostos para um ano"     ;Represents a documentation of the function (Docstring)
  [initial-amount annual-rate]                 ;List of parameters / Note: Runtime type inference 
  (let [rate (/ annual-rate 100)               ;Defining bindings - rate is the name of the local variable (let)
        result (* initial-amount (+ 1 rate))]  ;Formula: P*(1+r)
    (round-to-3-decimals result)))          

(defn calculate-interest-for-multiple-years
  "Calculo de juros compostos para multiplos anos"
  [initial-amount annual-rate years]
  (loop [year 1
         current-balance initial-amount
         yearly-details  []] 
    (if (> year years)
      yearly-details  ;"Return yearly-details if true"
      (let [new-balance     (calculate-interest-for-year current-balance annual-rate)
            interest-earned (round-to-3-decimals (- new-balance current-balance))
            year-detail     {:year                year  ;year-detils represents a key/value map
                             :starting-balance    current-balance
                             :interest-earned     interest-earned
                             :contributions-added 0.0
                             :ending-balance      new-balance}]
        (recur (inc year)
               new-balance
               (conj yearly-details year-detail))))))  ;Adding the element to the collection in an immutable way / "conj" = "conjoins"

(defn process-request
  "Processamento da requisicao e retorno de uma resposta"
  [^CompoundInterestRequestDTO request]
  (let [
        ;; Extraction of request data
        initial-amount (.getInitialAmount request) ;Java method call
        annual-rate (.getAnnualInterestRate request)
        years (.getYears request)

        yearly-details (calculate-interest-for-multiple-years initial-amount annual-rate years) ;Return an array of maps with details for each year

        final-balance (:ending-balance (last yearly-details)) ;Get the last element of the array
        total-interest (round-to-3-decimals (- final-balance initial-amount))

        ;; Creating java objects for response (yearly-dtos and calculation-dto)
        yearly-dtos (map (fn [detail]  ;Will return a new collection with the results 
                           (new YearlyBalanceDTO ;Creating a new instance of this class
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
  
  ;; Return response object
  (new CompoundInterestResponseDTO calculation-dto yearly-dtos)))

;; Function that will be called by the Java code in the configuration (external use -> ^:export)
(defn ^:export create-service 
  "Criacao de uma instancia do servico que implementa a interface java"
  []
  (reify CompoundInterestService  ;Creation of an anonymous instance (object Java) that implements such interface
    (calculateCompoundInterest [this request] ;Method name defined in Java interface / "request" is the CompoundInterestRequestDTO parameter 
      (process-request request))))