(ns push.types.base.string
  (:require [push.instructions.core :as core])
  (:require [push.types.core :as t])
  (:require [push.instructions.dsl :as d])
  (:require [clojure.string :as strings])
  )


;; string-specific
; assemblers and disassemblers

; exec_string_iterate
; string_conjchar
; string_contains
; string_containschar
; string_fromboolean
; string_fromchar
; string_fromfloat
; string_frominteger
; string_parse_to_chars
; string_readchar
; string_readline
; string_removechar
; string_replace
; string_replacechar
; string_replacefirst
; string_replacefirstchar
; string_rest
; string_setchar
; string_split
; string_substring
; string_take
; string_whitespace



(def string-concat (t/simple-2-in-1-out-instruction :string "concat" 'str))


(def string-butlast (t/simple-1-in-1-out-instruction :string "butlast"
                      #(clojure.string/join (butlast %1))))


(def string-emptystring? (t/simple-1-in-predicate :string "emptystring?" empty?))


(def string-first
  (core/build-instruction
    string-first
    :tags #{:string :base}
    (d/consume-top-of :string :as :arg1)
    (d/calculate [:arg1] #(first %1) :as :c)
    (d/push-onto :char :c)))


(def string-indexofchar
  (core/build-instruction
    string-indexofchar
    :tags #{:string :base}
    (d/consume-top-of :string :as :s)
    (d/consume-top-of :char :as :c)
    (d/calculate [:s :c] #(.indexOf %1 (int %2)) :as :where)
    (d/push-onto :integer :where)))


(def string-last
  (core/build-instruction
    string-last
    :tags #{:string :base}
    (d/consume-top-of :string :as :arg1)
    (d/calculate [:arg1] #(last %1) :as :c)
    (d/push-onto :char :c)))


(def string-length
  (core/build-instruction
    string-length
    :tags #{:string :base}
    (d/consume-top-of :string :as :arg1)
    (d/calculate [:arg1] #(count %1) :as :len)
    (d/push-onto :integer :len)))


(def string-nth
  (core/build-instruction
    string-nth
    :tags #{:string :base}
    (d/consume-top-of :string :as :s)
    (d/consume-top-of :integer :as :where)
    (d/calculate [:s :where] #(mod %2 (count %1)) :as :idx)
    (d/calculate [:s :idx] #(nth %1 %2) :as :result)
    (d/push-onto :char :result)))


(def string-occurrencesofchar
  (core/build-instruction
    string-occurrencesofchar
    :tags #{:string :base}
    (d/consume-top-of :string :as :s)
    (d/consume-top-of :char :as :c)
    (d/calculate [:s :c] #(get (frequencies %1) %2 0) :as :count)
    (d/push-onto :integer :count)))


(def string-reverse (t/simple-1-in-1-out-instruction :string "reverse" 'strings/reverse))


(def classic-string-type
  ( ->  (t/make-type  :string
                      :recognizer string?
                      :attributes #{:string :base})
        t/make-visible 
        t/make-equatable
        t/make-comparable
        t/make-movable
        (t/attach-instruction , string-butlast)
        (t/attach-instruction , string-concat)
        (t/attach-instruction , string-emptystring?)
        (t/attach-instruction , string-first)
        (t/attach-instruction , string-indexofchar)
        (t/attach-instruction , string-last)
        (t/attach-instruction , string-length)
        (t/attach-instruction , string-nth)
        (t/attach-instruction , string-occurrencesofchar)
        (t/attach-instruction , string-reverse)
        ))

