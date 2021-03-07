(set-logic QF_UFLIA)

; TWO + TWO = FOUR
(push)
(declare-fun T () Int)
(declare-fun W () Int)
(declare-fun O () Int)
(declare-fun F () Int)
(declare-fun U () Int)
(declare-fun R () Int)

(assert (= (+ T W O T W O) (+ F O U R)))

(check-sat)
(get-value (T W O))
(get-value (F O U R))
(pop)
