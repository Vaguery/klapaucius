(ns push.instructions.core
  (:require [push.util.exceptions :as oops])
  (:use [push.instructions.dsl])
  )


;;;; integration of DSL with instruction definition


(defn needs-of-dsl-step
  [step]
  (let [cmd (first step)
        resolved (resolve cmd)]
    (condp = resolved
      #'calculate {}
      #'consume-nth-of {(second step) 1}
      #'consume-stack {(second step) 0}
      #'consume-top-of {(second step) 1}
      #'count-of {(second step) 0}
      #'delete-nth-of {(second step) 1}
      #'delete-stack {(second step) 0}
      #'delete-top-of {(second step) 1}
      #'insert-as-nth-of {(second step) 0}
      #'replace-stack {(second step) 0}
      #'push-onto {(second step) 0}
      #'push-these-onto {(second step) 0}
      #'save-nth-of {(second step) 1}
      #'save-stack {(second step) 0}
      #'save-top-of {(second step) 1}
      (oops/throw-unknown-DSL-exception cmd)  )))


(defn total-needs
  [transaction]
  (apply (partial merge-with +)
    (map needs-of-dsl-step transaction)))


(defmacro
  def-function-from-dsl
  [& transactions]
  (let [interpreter (gensym 'interpreter)
       words &form]
    (do 
    `(fn [~interpreter] 
      (first (-> [~interpreter {}] ~@transactions))))))


(defrecord Instruction [token docstring tags needs transaction])


(defn make-instruction
  "creates a new Instruction record instance"
  [token & {
    :keys [docstring tags needs transaction] 
    :or { docstring "This really should have docs"
          tags #{}
          needs {}
          transaction identity }}]
  (->Instruction token docstring tags needs transaction))


(defmacro build-instruction
  "Takes a token and zero or more transaction steps, and
  creates the named instruction from those steps."
  [new-name & steps]
  (if (= :tags (first steps))
    (let [tags (second steps)
          steps (drop 2 steps)]
    `(make-instruction (keyword ~(name new-name))
      :tags ~tags
      :needs ~(total-needs steps)
      :transaction (def-function-from-dsl ~@steps)))
    `(make-instruction (keyword ~(name new-name))
      :needs ~(total-needs steps)
      :transaction (def-function-from-dsl ~@steps))))

