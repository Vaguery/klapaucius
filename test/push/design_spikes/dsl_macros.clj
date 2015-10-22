(ns push.design-spikes.dsl-macros
  (:use midje.sweet)
  (:use push.interpreter))

;; This is a design spike. That means it's NOT TO BE USED.
;; IT'S A SKETCH.

;; So I'm thinking about each Push instruction as a cascade of individual
;; atomic transformations, using Clojure's thread-first macro to apply
;; a sequence of DSL functions.

;; The DSL for Push instructions could be a series of simple steps.
;; Everything would be assumed to happen inside one interpreter, and the interpreter
;; (and some store of local scratch vars) would be passed step-to-step.
;;
; - `count-of [stackname :as local]`
;    store the number of items in `stackname` under key `local`
; - `consume [stackname]`
;    pop an item (and discard it) from `stackname`
;    raise an Exception if it's empty or undefined
; - `consume [stackname :as local]`
;    pop an item from `stackname` and store under key `local`
;    raise an Exception if it's empty or undefined
; - `consume [stackname :at where :as local]`
;    delete an item from `stackname` at position `where` and store under key `local`
;    raise an Exception if it's empty or undefined
; - `consume-stack [stackname]`
;    clear the named stack
;    raise an Exception if it's undefined
; - `consume-stack [stackname :as local]`
;    save the entire stack into `local` and clear it
;    raise an Exception if it's undefined
; - `remember [stackname :as local]`
;    store top item from `stackname` under key `local`
;    raise an Exception if it's empty or undefined
; - `remember [stackname :at where :as local]`
;    store item in `stackname` at position `where` under key `local`
;    raise an Exception if it's empty or undefined
; - `remember-stack [stackname :as local]`
;    save the entire stack to the `local`
;    raise an Exception if it's undefined
; - `place [stackname item]`
;    push the item to the named stack
;    raise an Exception if it's undefined
; - `replace-stack [stackname new-stack]`
;    replace the indicated stack with a new list
;    raise an exception if it's undefined

;; And I THINK that supports all defined Push instructions I've ever seen....


(def six-ints (make-interpreter :stacks {:integer '(6 5 4 3 2 1)}))


(defrecord PushDslState [interpreter locals])


(defn make-PushDslState
  "creates a PushDslState record"
  ([interpreter] (make-PushDslState interpreter {}))
  ([interpreter locals] (->PushDslState interpreter locals)))


(defn consume
  [dslstep stack & {:keys [as]}]
  (let [interpreter (:interpreter dslstep)
        old-stack (get-stack interpreter stack)
        old-locals (:locals dslstep)]
    (if (empty? old-stack)
      (throw (Exception. (str "Push DSL Error: " stack " is empty")))
      (make-PushDslState
        (set-stack interpreter stack (pop old-stack))
        (assoc old-locals as (first old-stack)))
    )))


(def sixier (make-PushDslState six-ints))


(fact "consume returns a new PushDslState with a changed :interpreter and new locals"
  (:locals (consume sixier :integer :as :int1)) => {:int1 6}
  (get-stack (:interpreter (consume sixier :integer :as :int1)) :integer) => '(5 4 3 2 1))


(fact "consume throws an exception if the stack is empty"
  (:locals (consume sixier :boolean :as :nope)) => (throws #"Push DSL Error:"))


(fact "consume can be thread-firsted"
  (let [two-popped (->  sixier
                        (consume :integer :as :int1)
                        (consume :integer :as :int2))]
    (:locals two-popped) => {:int1 6, :int2 5}
    (get-stack (:interpreter two-popped) :integer) => '(4 3 2 1)))


(defn place
  [dslstep stack args function]
  (let [result (apply function (map (:locals dslstep) args))
        interpreter (:interpreter dslstep)]
    (assoc dslstep :interpreter (push-item interpreter stack result))))


(fact "`place` will apply the specified inline function to the dslstate locals named"
  (let [integer-add (-> sixier
                        (consume :integer :as :int1)
                        (consume :integer :as :int2)
                        (place :integer [:int1 :int2] #(+ %1 %2)))]
  (get-stack (:interpreter integer-add) :integer) => '(11 4 3 2 1))) ;; 


; (def-pushinstruction
;   integer-add
;   :doc "adds two :integers"
;   :needs {:integer 2}
;   :makes {:integer 1}
;   :tags [:arithmetic :core]
;     (consume :integer :as :int1)
;     (consume :integer :as :int2)
;     (place :integer [:int1 :int2] %(+ %1 %2))
;     )


; (def-pushinstruction
;   boolean-flush
;   :doc "empties the :boolean stack"
;   :needs {:boolean 0}
;   :tags [:combinator :core]
;   :transaction
;     (consume-stack :boolean)
;     )


; (def-pushinstruction
;   float-yankdup
;   :doc "Takes an :integer, and copies the indicated nth item (modulo the :float stack size) on the :float stack to the top; so if the :integer is 12 and the :float stack has 5 items, the (mod 12 5) item is copied to the top as a new 6th item."
;   :needs {:integer 1 :float 1}
;   :tags [:core :combinator]
;   :transaction
;     (consume :integer :as :int1)
;     (count-of :float :as :how-many)
;     ????? this one's not so obvious yet
;     (remember :float :at (mod int1 ) :as :float1)
;     (place :float float1)]
;     )


; (def-pushinstruction
;   exec-noop
;   :doc "Does nothing."
;   :tags [:core]
;   ;; everything else is default behavior
;   )