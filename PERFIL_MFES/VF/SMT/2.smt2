(set-logic QF_UFLIA)

(declare-fun mythical () Bool)
(declare-fun immortal () Bool)
(declare-fun mammal () Bool)
(declare-fun horned () Bool)
(declare-fun magical () Bool)

(assert (=> mythical immortal))
(assert (=> (not mythical) (and (not immortal) mammal)))
(assert (=> (or immortal mammal) horned))
(assert (=> magical horned))

(check-sat)

; Is the unicorn magical?
; True if unsat
; sat -> False
(push)
(assert (not magical))
(check-sat)
(pop)

; Is the unicorn horned?
; True if unsat
; unsat -> True
(push)
(assert (not horned))
(check-sat)
(pop)

; Is the unicorn horned?
; True if unsat
; sat -> False
(push)
(assert (not mythical))
(check-sat)
(pop)
