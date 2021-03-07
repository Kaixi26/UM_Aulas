(set-logic QF_AUFLIA)

(declare-const a0 (Array Int Int))
(declare-const a1 (Array Int Int))
(declare-const a2 (Array Int Int))
(declare-const i Int)
(declare-const x Int)
(declare-const y0 Int)
(declare-const y1 Int)

; x = a[i]
(assert (= x (select a0 i)))
; y = y + x
(assert (= y1 (+ y0 x)))
; a[i] = 5 + a[i]
(assert (= a1 (store a0 i (+ 5 (select a0 i)))))
; a[i+1] = a[i-1] - 5;
(assert (= a2 (store a1 (+ i 1) (- (select a1 (- i 1)) 5))))

(check-sat)

(push)
(echo "1. True if unsat")
; x + a[i-1] = a[i] + a[i+1]
(assert (not (= (+ x (select a2 (- i 1)))
                (+ (select a2 i) (select a2 (+ i 1))))))
(check-sat)
(pop)

(push)
(echo "2. True if unsat")
(assert (not (<= 0 (+ (select a2 (- i 1)) (select a2 i)))))
(check-sat)
(pop)

(push)
(echo "3. True if unsat")
(assert (not (=> (< y1 5)
                 (< y1 (select a2 i)))))
(check-sat)
(pop)
