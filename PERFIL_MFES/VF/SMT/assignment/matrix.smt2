(set-logic QF_AUFLIA)
; M[1][1] = 2;
; M[1][2] = 3;
; M[1][3] = 4;
; M[2][1] = 3;
; M[2][2] = 4;
; M[2][3] = 5;
; M[3][1] = 4;
; M[3][2] = 5;
; M[3][3] = 6;

(declare-const M0 (Array Int (Array Int Int)))
(declare-const M1 (Array Int (Array Int Int)))
(declare-const i Int)
(declare-const j Int)

(define-fun assignM
  ((x Int) (y Int) (val Int) (m (Array Int (Array Int Int))))
  (Array Int (Array Int Int))
  (store m x (store (select m x) y val)))

(define-fun accessM
  ((x Int) (y Int) (m (Array Int (Array Int Int))))
  Int
  (select (select M1 y) x))


(assert (= M1
           (assignM 1 1 2
           (assignM 1 2 3
           (assignM 1 3 4
           (assignM 2 1 3
           (assignM 2 2 4
           (assignM 2 3 5
           (assignM 3 1 4
           (assignM 3 2 5
           (assignM 3 3 6 M0)))))))))))

(check-sat)

(push)
(echo "") (echo "Se i=j então M[i][j] /= 3.")
(echo "Afirmação falsa pois negação é sat.")
(assert (not (=> (= i j) (not (= 3 (accessM i j M1))))))
(check-sat)
(pop)

(push)
(assert (and (<= 1 i) (<= i 3)))
(assert (and (<= 1 j) (<= j 3)))
(echo "") (echo "M[i][j] = M[j][i]")
(echo "Afirmação verdadeira pois negação é unsat.")
(assert (not (= (accessM i j M1) (accessM i j M1))))
(check-sat)
(pop)

(push)
(assert (and (<= 1 i) (<= i 3)))
(assert (and (<= 1 j) (<= j 3)))
(echo "") (echo "i<j então M[i][j] < 6")
(echo "Afirmação verdadeira pois negação é unsat.")
(assert (not (=> (< i j) (< (accessM i j M1) 6))))
(check-sat)
(pop)

(push)
(assert (and (<= 1 i) (<= i 3)))
(declare-const a Int)
(declare-const b Int)
(assert (and (<= 1 a) (<= a 3)))
(assert (and (<= 1 b) (<= b 3)))
(echo "") (echo "a > b então M[i][a] > M[i][b]")
(echo "Afirmação verdadeira pois negação é unsat.")
(assert (not (=> (> a b) (> (accessM i a M1) (accessM i b M1)))))
(check-sat)
(pop)

(push)
(assert (and (<= 1 i) (<= i 3)))
(assert (and (<= 1 j) (<= j 3)))
(echo "") (echo "M[i][j] + M[i+1][j] = M[i+1][j] + M[i][j+1]")
(echo "Afirmação falsa pois negação é sat.")
(assert (not (=
              (+ (accessM i j M1) (accessM (+ i 1) (+ j 1) M1))
              (+ (accessM (+ i 1) j M1) (accessM i (+ j 1) M1)))))
(check-sat)
(pop)
