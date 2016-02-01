(ns push.instructions.core
  (:require [push.util.exceptions :as oops])
  (:require [push.util.general :as util])
  (:use [push.instructions.dsl])
  )


;;;; integration of DSL with instruction definition


(defn needs-of-dsl-step
  [step]
  (let [cmd (first step)
        resolved (resolve cmd)]
    (condp = resolved
      #'archive-all-stacks {:environment 0}
      #'bind-item {}
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
      #'record-an-error {}
      #'retrieve-all-stacks {}
      #'save-max-collection-size {}
      #'save-counter {}
      #'save-bindings {}
      #'save-instructions {}
      #'save-nth-of {(second step) 1}
      #'save-stack {(second step) 0}
      #'save-top-of {(second step) 1}
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
      #'archive-all-stacks {}
      #'bind-item {}
      #'calculate {}
      #'consume-nth-of {}
      #'consume-stack {}
      #'consume-top-of {}
      #'count-of {}
      #'delete-nth-of {}
      #'delete-stack {}
      #'delete-top-of {}
      #'insert-as-nth-of {(second step) 1}
      #'replace-stack {(second step) 0}
      #'push-onto {(second step) 1}
      #'push-these-onto {(second step) (count (last step))}
      #'record-an-error {:log 1}
      #'retrieve-all-stacks {}
      #'save-counter {:integer 1}
      #'save-bindings {}
      #'save-instructions {}
      #'save-max-collection-size {}
      #'save-nth-of {}
      #'save-stack {}
      #'save-top-of {}
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
      (first (-> [~interpreter {}] ~@transactions))))))


(defrecord Instruction [token docstring tags needs products transaction])


(defn make-instruction
  "creates a new Instruction record instance"
  [token & {
    :keys [docstring tags needs products transaction] 
    :or { docstring (str "`" token "` needs a docstring!")
          tags #{}
          needs {}
          products {}
          transaction identity }}]
  (with-meta (->Instruction token docstring tags needs products transaction) {:doc docstring}))


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


