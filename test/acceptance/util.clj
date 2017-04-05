(ns acceptance.util
  (:require [push.core :as push]
            [push.type.definitions.interval :as iv]
            [push.type.definitions.complex :as complex]
            [push.type.definitions.quoted :as qc])
  (:use [midje.sweet])
  )


;; literal generators

(defn some-boolean
  []
  (rand-nth [true false]))


(defn some-long
  "returns a random long selected from [-max/2,max/2]"
  [i]
  (-' (rand-int i) (/ i 2)))


(defn some-double
  "returns a random double selected from [-max/2,max/2]"
  [i]
  (-' (rand i) (/ i 2)))


(defn some-rational
  "returns a random rational composed of numerator `some-long` and a positive random integer in [1,range]"
  [i]
  (/ (some-long i) (inc (rand-int i))))


(defn some-bigint
  "returns a random BigInt based on `(some-long range)`"
  [i]
  (bigint (some-long i)))


(defn some-bigdec
  "returns a random BigDecimal based on `(some-double range)`"
  [i]
  (bigdec (some-double i)))


(defn some-ascii
  "returns a random character in the range [33,125]"
  []
  (char (+ 33 (rand-int (- 126 33)))))


(defn some-string
  "returns a random string of length i, by assembling characters picked with `(some-ascii)`"
  [i]
  (clojure.string/join
    (take i
      (repeatedly #(char (+ 33 (rand-int (- 126 33))))))))


(defn vector-of-stuff
  "constructs a vector of a given length by calling a function with the given argument repeatedly; for example, `(vector-of-stuff some-long 3 10)` will produce a 10-element vector of numbers from the range [-1,1]"
  [f i j]
  (into [] (take j (repeatedly #(f i)))))


(defn some-instruction
  "given an interpreter instance, it randomly selects one if the defined instructions from its keys"
  [interpreter]
  (rand-nth (push/known-instructions interpreter)))


(defn some-interval
  "given a range, produces an Interval record using `(some-long)` and `(some-double)` for the range"
  [i]
  (iv/make-interval (some-long i) (some-double i)))


(def all-the-variable-names
  (map keyword
       (map str (map char (range 97 123)))))

(defn some-ref
  "given an interpreter instance, it randomly selects one if the defined bindings, plus one from an additional collection of keywords"
  [interpreter extra-bindings]
  (rand-nth
    (concat
      (keys (:bindings interpreter))
      (keys (:bindings interpreter))
      (keys (:bindings interpreter))
      (keys (:bindings interpreter))
      all-the-variable-names)))



;; code generation

(declare some-codeblock)

(defn some-item
  [scale erc-prob interpreter]
  (if (< (rand) erc-prob)
    (rand-nth [
      (some-boolean)
      (some-long scale)
      (some-long (* 10 scale))
      (some-long (* 1000 scale))
      (some-double scale)
      (some-double (* 10 scale))
      (some-double (* 1000 scale))
      (some-rational (* 100 scale))
      (some-bigint (* 10000 scale))
      (some-bigdec (* 10000 scale))
      (complex/complexify
        (some-long scale) (some-rational scale))
      (some-ascii)
      (some-string scale)
      (some-interval (* 100 scale))
      (vector-of-stuff some-long (* 10 scale) (rand-int scale))
      (vector-of-stuff some-double (* 10 scale) (rand-int scale))
      (some-codeblock (dec scale) erc-prob interpreter)
      (qc/push-quote (some-codeblock (dec scale) erc-prob interpreter))
      ])
    (if (< (rand) erc-prob)
      (some-ref interpreter all-the-variable-names)
      (some-instruction interpreter)
      )))


(defn some-codeblock
  [scale erc-prob interpreter]
  (take
    (rand-int scale)
    (repeatedly #(some-item scale erc-prob interpreter))
    ))


(defn some-program
  [prog-size scale erc-prob interpreter]
  (into []
    (take
      prog-size
      (repeatedly #(some-item scale erc-prob interpreter))
      )))


(defn some-bindings
  [how-many scale erc-prob interpreter]
  (zipmap
      (take how-many all-the-variable-names)
      (repeatedly #(some-item scale erc-prob interpreter))
      ))


(defn preloaded-stacks
  [interpreter how-many scale erc-prob]
  {
    :boolean (repeatedly how-many some-boolean)
    :booleans (repeatedly how-many
                #(into []
                  (repeatedly (rand-int how-many) some-boolean)))
    :char (repeatedly how-many some-ascii)
    :chars (repeatedly how-many
            #(into []
              (repeatedly (rand-int how-many) some-ascii)))
    :code (repeatedly how-many
            #(some-codeblock (inc (rand-int how-many)) erc-prob interpreter))
    :complex (repeatedly how-many
              #(complex/complexify (some-long scale) (some-rational scale)))
    :complexes (repeatedly how-many
                (fn []
                  (into [] (repeatedly (inc (rand-int how-many))
                            #(complex/complexify (some-rational scale)
                              (some-double scale))))))
    :ref (repeatedly how-many
          #(some-ref interpreter all-the-variable-names))
    ; ; :refs '()
    :scalar (repeatedly how-many #(some-long (* 10 scale)))
    :scalars (repeatedly how-many
              (fn [] (into [] (repeatedly (rand-int how-many)
                #(some-long (* 10 scale))))))
    ; :set '()
    :string (repeatedly how-many #(some-string scale))
    :strings (repeatedly how-many
                (fn [] (into [] (repeatedly (rand-int how-many) #(some-string scale)))))
    :vector (repeatedly how-many
              (fn []
                (into []
                  (repeatedly how-many
                    #(some-codeblock (inc (rand-int how-many)) erc-prob interpreter)))))
    }
  )

;
; (println (str (preloaded-stacks (push/interpreter :bindings {:x 9 :y 99}) 20 10 4/5)))
