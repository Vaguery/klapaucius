(ns push.design-spikes.dsl-macros
  (:use midje.sweet)
  (:use push.interpreter))

;; This is a design spike. That means it's NOT TO BE USED.
;; IT'S A SKETCH.

;; So I'm thinking about each Push instruction as a
;; "transaction" involving a single Interpreter passing in and out.

;; The DSL for Push instructions could be a series of threaded (->) steps.
;; Everything would be assumed to happen inside one interpreter, and the interpreter
;; (and some kind of local vars) would be passed step-to-step.
;; - `count-of` would return the number of items of the named stack
;; + `consume-as` would pop an item from the named stack and assign it to a (local) symbol
;; - `consume-nth-as` would extract a numbered item from the stack and record it
;; - `consume-stack-as` would grab the entire list as a single (local) symbol
;; - `record-as` would save the current top item on the named stack
;; - `record-nth-as` would save the nth item on the named stack
;; - `discard` would pop an item from the named stack
;; - `discard-stack` would empty the named stack
;; + `send-to` would push the indicated value to that stack
;;   (using the router and any filters found there)
;; - `replace-stack` would replace the indicated stack with a new list

;; And I THINK that supports all defined Push instructions I've ever seen.


(def six-ints (make-interpreter :stacks {:integer '(6 5 4 3 2 1)}))


(defn pop-item
  [interpreter stack]
  (let [old-stack (get-stack interpreter stack)]
    (if (empty? old-stack)
      [interpreter nil]
      [(set-stack interpreter stack (pop old-stack)) (first old-stack)]
    )))


(fact "pop-item returns a vector of the item (or nil) and the changed interpreter"
  (first (pop-item six-ints :integer)) => (set-stack six-ints :integer '(5 4 3 2 1))
  (last  (pop-item six-ints :integer)) => 6
  )

(defrecord PushDslState [interpreter locals])


(defn make-PushDslState
  "creates a PushDslState record"
  ([interpreter] (make-PushDslState interpreter {}))
  ([interpreter locals] (->PushDslState interpreter locals)))


(defn consume-as
  [dslstep id stack]
  (let [interpreter (:interpreter dslstep)
        old-stack (get-stack interpreter stack)
        old-locals (:locals dslstep)]
    (if (empty? old-stack)
      (throw (Exception. (str "Push DSL Error: " stack " is empty")))
      (make-PushDslState
        (set-stack interpreter stack (pop old-stack))
        (assoc old-locals id (first old-stack)))
    )))


(def sixier (make-PushDslState six-ints))


(fact "consume-as returns a new PushDslState with a changed :interpreter and new locals"
  (:locals (consume-as sixier :int1 :integer)) => {:int1 6}
  (get-stack (:interpreter (consume-as sixier :int1 :integer)) :integer) => '(5 4 3 2 1))


(fact "consume-as throws an exception if the stack is empty"
  (:locals (consume-as sixier :nope :boolean)) => (throws #"Push DSL Error:"))


(fact "consume-as can be thread-firsted"
  (let [two-popped (-> sixier
                      (consume-as :int1 :integer)
                      (consume-as :int2 :integer))]
    (:locals two-popped) => {:int1 6, :int2 5}
    (get-stack (:interpreter two-popped) :integer) => '(4 3 2 1)))


(defn send-to
  [dslstep stack args function]
  (let [result (apply function (map (:locals dslstep) args))
        interpreter (:interpreter dslstep)]
    (assoc dslstep :interpreter (push-item interpreter stack result))))


(fact "`send-to` will apply the specified inline function to the dslstate locals named"
  (let [added (-> sixier
                    (consume-as :int1 :integer)
                    (consume-as :int2 :integer)
                    (send-to :integer [:int1 :int2] #(+ %1 %2)))]
  (get-stack (:interpreter added) :integer) => '(11 4 3 2 1)))

;; now there's another possibility that strikes me:
;; we could use a syntax like (consume % :integer :as :int1)
;; and that would permit      (consume % :integer)
;; which might put the consumed item in a little vector stack
;;
;; why?
;; because then integer-add might be
;;   -> interpreter 
;;      (consume :integer)
;;      (consume :integer)
;;      #(+ %1 %2)           (which would use the stack as args)
;; but that might be much harder to read :/


;; a simpler version yet (using an invisible stack inside the Interpreter itself, maybe?):
;; (grab :integer)
;; (grab :integer)
;; (place :integer #(+ %1 %2))




; (def-pushinstruction
;   integer-add
;   :doc "adds two :integers"
;   :needs {:integer 2}
;   :makes {:integer 1}
;   :tags [:arithmetic :core]
;     (consume-as :int1 :integer)
;     (consume-as :int2 :integer)
;     (send-to :integer [:int1 :int2] %(+ %1 %2))
;     )


; ;; expands to a function something like

; (defn integer_add
;   "adds two :integers"
;   [interpreter]
;   (-> interpreter 
;     (consume-as int1 :integer)
;     (consume-as int2 :integer)
;     (send-to :integer (+ int1 int2))))

; ;;

; (def-pushinstruction
;   boolean-flush
;   :doc "empties the :boolean stack"
;   :needs {:boolean 0}
;   :tags [:combinator :core]
;   :transaction
;     (discard-stack :boolean)
;     )

; ;; expands to a function something like

; (defn boolean_flush
;   "empties the :boolean stack"
;   [interpreter]
;   (-> interpreter (discard-stack :boolean))
;   )


; (def-pushinstruction
;   float-yankdup
;   :doc "Takes an :integer, and copies the indicated nth item (modulo the :float stack size) on the :float stack to the top; so if the :integer is 12 and the :float stack has 5 items, the (mod 12 5) item is copied to the top as a new 6th item."
;   :needs {:integer 1 :float 1}
;   :tags [:core :combinator]
;   :transaction
;     (consume-as int1 :integer)
;     (record-nth-as float1 :float (mod int1 (count-of :float)))
;     (send-to :float float1)]
;     )


; (def-pushinstruction
;   exec-noop
;   :doc "Does nothing."
;   :tags [:core]
;   ;; everything else is default behavior
;   )