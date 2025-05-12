#_"
current balance - saldo atual 
interest earned - juros ganho
"

;; Defining Namespace
(ns com.example.financial-calculator-fp.service.compound-interest-service
  (:import (com.example.financial_calculator_fp.service CompoundInterestService) 
           (com.example.financial_calculator_fp.model.request CompoundInterestRequestDTO)
           (com.example.financial_calculator_fp.model.response CompoundInterestResponsetDTO CalculationDTO YearlyBalanceDTO)))
 
(defn calculate-interest-for-year           ;; Name of function
  "Calculo de juros compostos para um ano"  ;; Represents a documentation of the function (Docstring)
  [initial-amount annual-rate]              ;; List of parameters / Note: Runtime type inference 
  (let [rate (/ annual-rate 100)]           ;; Defining bindings - rate is the name of the local variable (let)
    (* initial-amount (+ 1 rate))))         ;; Formula: P*(1+r) / Note: The last line is always returned by the function

(defn calculate-interest-for-multiple-years
  "Calculo de juros para multiplos anos"
  [initial-amount annual-rate years]
  (loop [year 1
         current-balance initial-amount
         yearly-details  []] 
    (if (> year years)
      yearly-details  ;; "return yearly-details if true"
      (let [new-balance     (calculate-interest-for-year current-balance annual-rate)
            interest-earned (- new-balance current-balance)
            year-detail     {:year                year             ;; year-detils represents a key/value map
                             :starting-balance    current-balance
                             :interest-earned     interest-earned
                             :contributions-added 0.0
                             :ending-balance      new-balance}]
        (recur (inc year)
               new-balance
               (conj yearly-details year-detail))))))     ;; Adding the element to the collection in an immutable way / "conj" = "conjoins"
        
