(ns push.instructions.base-test
  (:use midje.sweet)
  (:use [push.instructions.base])
  )

;; boolean instructions


(fact "boolean instructions are defined"
  (keys (ns-publics 'push.instructions.base.boolean)) => 
    (contains '(
      boolean-and
      boolean-not
      boolean-or
      boolean-xor
      ) :in-any-order))


(future-fact "all boolean instructions have docstrings")


(fact "conversion instructions are defined"
  (keys (ns-publics 'push.instructions.base.conversion)) => 
    (contains '(
      integer-fromboolean
      integer-fromchar
      integer-fromfloat
      ) :in-any-order))


(future-fact "all conversion instructions have docstrings")


(fact "integer instructions are defined"
  (keys (ns-publics 'push.instructions.base.integer)) => 
    (contains '(
      integer-add
      integer-dec
      integer-divide
      integer-dup
      integer-eq
      integer-empty?
      integer-flush
      integer-gt
      integer-gte
      integer-inc
      integer-lt
      integer-lte
      integer-max
      integer-min
      integer-mod
      integer-multiply
      integer-pop
      integer-rotate
      integer-shove
      integer-stackdepth
      integer-subtract
      integer-swap
      integer-yank
      integer-yankdup
      ) :in-any-order))


(future-fact "all integer instructions have docstrings")

