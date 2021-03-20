(declare-const x11 Int)
(declare-const x12 Int)
(declare-const x13 Int)
(declare-const x14 Int)
(declare-const x21 Int)
(declare-const x22 Int)
(declare-const x23 Int)
(declare-const x24 Int)
(declare-const x31 Int)
(declare-const x32 Int)
(declare-const x33 Int)
(declare-const x34 Int)
(declare-const x41 Int)
(declare-const x42 Int)
(declare-const x43 Int)
(declare-const x44 Int)

(assert (and (<= 1 x11) (<= x11 4)))
(assert (and (<= 1 x12) (<= x12 4)))
(assert (and (<= 1 x13) (<= x13 4)))
(assert (and (<= 1 x14) (<= x14 4)))
(assert (and (<= 1 x21) (<= x21 4)))
(assert (and (<= 1 x22) (<= x22 4)))
(assert (and (<= 1 x23) (<= x23 4)))
(assert (and (<= 1 x24) (<= x24 4)))
(assert (and (<= 1 x31) (<= x31 4)))
(assert (and (<= 1 x32) (<= x32 4)))
(assert (and (<= 1 x33) (<= x33 4)))
(assert (and (<= 1 x34) (<= x34 4)))
(assert (and (<= 1 x41) (<= x41 4)))
(assert (and (<= 1 x42) (<= x42 4)))
(assert (and (<= 1 x43) (<= x43 4)))
(assert (and (<= 1 x44) (<= x44 4)))

; distinct lines
(assert (distinct x11 x12 x13 x14))
(assert (distinct x21 x22 x23 x24))
(assert (distinct x31 x32 x33 x34))
(assert (distinct x41 x42 x43 x44))

; distinct cols
(assert (distinct x11 x21 x31 x41))
(assert (distinct x12 x22 x32 x42))
(assert (distinct x13 x23 x33 x43))
(assert (distinct x14 x24 x34 x44))

; constrains
(assert (> x21 x11))
(assert (> x21 x13))
(assert (> x22 x23))
(assert (> x43 x44))
(assert (> x14 x13))
(assert (> x24 x34))

; fixed numbers
(assert (= 3 x14))

(check-sat)
(get-value (x11 x21 x31 x41 x12 x22 x32 x42 x13 x23 x33 x43 x14 x24 x34 x44))
