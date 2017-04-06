(ns push.instructions.core
  (:require [push.util.exceptions
              :as oops]
            [push.util.general
              :as util])
  (:use     [push.instructions.dsl]
            ))


;;;; integration of DSL with instruction definition


(defn needs-of-dsl-step
  [step]
  (let [cmd (first step)
        resolved (resolve cmd)]
    (condp = resolved
      #'bind-item {}
      #'calculate {}
      #'clear-binding {}
      #'consume-nth-of {(second step) 1}
      #'consume-stack {}
      #'consume-top-of {(second step) 1}
      #'count-of {}
      #'delete-nth-of {(second step) 1}
      #'delete-stack {}
      #'delete-top-of {}
      #'forget-binding {}
      #'insert-as-nth-of {}
      #'replace-stack {}
      #'push-onto {}
      #'push-these-onto {}
      #'quote-all-bindings {}
      #'quote-no-bindings {}
      #'record-an-error {}
      #'replace-binding {}
      #'retrieve-snapshot-state {}
      #'save-binding-stack {}
      #'save-bindings {}
      #'save-counter {}
      #'save-instructions {}
      #'save-max-collection-size {}
      #'save-nth-of {(second step) 1}
      #'save-snapshot {}
      #'save-stack {}
      #'save-top-of {(second step) 1}
      #'save-top-of-binding {}
      #'start-cycling-arguments {}
      #'start-storing-arguments {}
      #'stop-cycling-arguments {}
      #'stop-storing-arguments {}
      (oops/throw-unknown-DSL-exception cmd)  )))


(defn total-needs
  [transaction]
  (apply (partial merge-with +)
    (map needs-of-dsl-step transaction)))


(defn products-of-dsl-step
  [step]
  (let [cmd (first step)
        resolved (resolve cmd)]
    (condp = resolved
      #'bind-item {}
      #'calculate {}
      #'clear-binding {}
      #'consume-nth-of {}
      #'consume-stack {}
      #'consume-top-of {}
      #'count-of {}
      #'delete-nth-of {}
      #'delete-stack {}
      #'delete-top-of {}
      #'forget-binding {}
      #'insert-as-nth-of {(second step) 1}
      #'replace-stack {}
      #'push-onto {(second step) 1}
      #'push-these-onto {(second step) (count (last step))}
      #'quote-all-bindings {}
      #'quote-no-bindings {}
      #'record-an-error {:log 1}
      #'replace-binding {}
      #'retrieve-snapshot-state {}
      #'save-binding-stack {}
      #'save-bindings {}
      #'save-counter {:scalar 1}
      #'save-instructions {}
      #'save-max-collection-size {}
      #'save-nth-of {}
      #'save-snapshot {}
      #'save-stack {}
      #'save-top-of {}
      #'save-top-of-binding {}
      #'start-cycling-arguments {}
      #'start-storing-arguments {}
      #'stop-cycling-arguments {}
      #'stop-storing-arguments {}
      (oops/throw-unknown-DSL-exception cmd)  )))


(defn total-products
  [transaction]
  (apply (partial merge-with +)
    (map products-of-dsl-step transaction)))


(defmacro
  def-function-from-dsl
  [& transactions]
  (let [interpreter (gensym 'interpreter)
       words &form]
    (do
    `(fn [~interpreter]
      (-> [~interpreter {:ARGS '()}] ~@transactions)))))


(defrecord Instruction [token docstring tags needs products transaction])


(defn make-instruction
  "creates a new Instruction record instance"
  [token & {
    :keys [docstring tags needs products transaction]
    :or { docstring (str "`" token "` needs a docstring!")
          tags #{}
          needs {}
          products {}
          transaction #(vector % {}) }}]
  (with-meta (->Instruction
                  token
                  docstring
                  tags
                  needs
                  products
                  transaction) {:doc docstring}))


(defmacro build-instruction
  "Takes a token and zero or more transaction steps in DSL form, and
  creates the named instruction from those steps. Keyword arguments
  must appear before DSL steps."
  [instruction & args]
  (let [new-kwd (keyword (name instruction))
        tags    (util/extract-keyword-argument :tags args)
        docs    (util/extract-docstring args)
        steps   (util/extract-splat-argument args)]
    `(make-instruction
      ~new-kwd
      :docstring ~(or docs (str "`" new-kwd "` needs a docstring!"))
      ;; TODO: can't seem to pass in `nil` here ^^^^^ and
      ;;       get a default string from `make-instruction`
      :tags ~(or tags #{})
      :needs ~(total-needs steps)
      :products ~(total-products steps)
      :transaction (def-function-from-dsl ~@steps))))
