(set-logic QF_UFLIA)

(declare-const a11 Int)
(declare-const a21 Int)
(declare-const a31 Int)
(declare-const a12 Int)
(declare-const a22 Int)
(declare-const a32 Int)
(declare-const a13 Int)
(declare-const a23 Int)
(declare-const a33 Int)
(declare-const N Int)

(define-fun visible ((a1 Int) (a2 Int) (a3 Int)) Int
  (ite (> a1 a2) 1
       (ite (> a2 a3) 2 3)))

(define-fun maximo ((x Int) (y Int)) Int (ite (> x y) x y))

(assert (= N 3))
(assert (and (<= 1 a11) (<= a11 N)))
(assert (and (<= 1 a21) (<= a21 N)))
(assert (and (<= 1 a31) (<= a31 N)))
(assert (and (<= 1 a12) (<= a12 N)))
(assert (and (<= 1 a22) (<= a22 N)))
(assert (and (<= 1 a32) (<= a32 N)))
(assert (and (<= 1 a13) (<= a13 N)))
(assert (and (<= 1 a23) (<= a23 N)))
(assert (and (<= 1 a33) (<= a33 N)))

(assert (distinct a11 a21 a31))
(assert (distinct a12 a22 a32))
(assert (distinct a13 a23 a33))

(assert (distinct a11 a12 a13))
(assert (distinct a21 a22 a23))
(assert (distinct a31 a32 a33))

(push)
(assert (= 1 (visible a31 a21 a11)))
(assert (= 2 (visible a13 a23 a33)))
(assert (= 3 (visible a33 a32 a31)))
(check-sat)
(get-value (a11 a21 a31 a12 a22 a32 a13 a23 a33))
(pop)
