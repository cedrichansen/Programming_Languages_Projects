(ns csc344hw02.core)


; Cedric Hansen
; CSC344 hw02
; october 2017



(defn remove-firsts [x]
  (let [y (rest x)]
    (flatten (map (fn [i] (if (list? i)
                            (remove-firsts i)
                            i)) y))
    ))

(defn only-numeric? [x]
  (every? number? (remove-firsts x)))


(defn numchecker [x]
  (if (list? x)
      (only-numeric? x)
      (number? x))
  )



(defn simplify [x]
  (let [op (first x)
        num1 (second x)
        num1 (if (seq? num1)
               (simplify num1)
               num1)
        num2 (last x)
        num2 (if (seq? num2)
               (simplify num2)
               num2)
        ]

    ;(* 1 x) => x
    ;(* x 1) => x
    ;(* 0 x) => 0
    ;(* x 0) => 0
    ;(+ 0 x) => x
    ;(+ x 0) => x
    ;(- (- x)) => x

    (cond
      (and (numchecker num1) (numchecker num2)(=  op '+))  (+ num1 num2)
      (and (numchecker num1) (numchecker num2) (= op '*)) (* num1 num2)
      (and (= num1 1) (= op '*)) num2
      (and (= num2 1) (= op '*)) num1
      (and (= num1 0) (= op '*)) 0
      (and (= num2 0) (= op '*)) 0
      (and (= num1 0) (= op '+)) num2
      (and (= num2 0) (= op '+)) num1
      :else (list op num1 num2)
      )
    )
  )



(defn transform [x y]
  (let [vector1 (nth x 0)
        vector2 (nth x 1)
        e1 (nth vector1 0)
        e2 (nth vector1 1)
        e3 (nth vector2 0)
        e4 (nth vector2 1)
        e5 (nth y 0)
        e6 (nth y 1)]

    (vector (simplify (list '+ (list '* e1 e5) (list '* e2 e6)))  (simplify (list '+ (list '* e3 e5) (list '* e4 e6))))
  )
  )
; reminder that when you input- put ' before letters
;(transform [['a 2] [3 4]] [5 6])
;=> [(+ (* a 5) (* 2 6)) (+ (* 3 5) (* 4 6))]



(defn bind-values [m l]
  (map (fn [i]
         (cond
           (seq? i) (bind-values m i)
           (vector? i) (vec (bind-values m i))
           :default (m i i)))
       l))


(defn evalexp [exp bindings]
  (simplify (bind-values bindings exp)))

;(evalexp '(transform [[a 3] [4 5]] [3 4]) '{a 5, y 2})
;(evalexp p1 '{a 5, y 2})


;(evalexp p3 '{x 5, y 2})


(def p1 '(transform [[a 3] [0 0]] [x y]))
(def p2 '(transform [[1 0] [0 (+ x 3)]] [(* x 2) y]))
(def p3 '(transform [[0 0] [1 1]]
                    (transform [[2 0] [0 2]]
                               (transform [[-1 0] [0 -1]] [x 2]))))
(def complicated (transform '[[0 0] [1 1]]
                    (transform '[[2 0] [0 2]]
                               (transform '[[-1 0] [0 -1]] '[x 2]))))