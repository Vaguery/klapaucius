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
;    raise an Exception if the stack doesn't exist
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
; - `remember [args fn :as local]
;    save the result of applying `fn` to the named `args` under key `local`
; - `remember [stackname :as local]`
;    store top item from `stackname` under key `local`
;    raise an Exception if it's empty or undefined
; - `remember [stackname :at where :as local]`
;    store item in `stackname` at position `where` under key `local`
;    raise an Exception if it's empty or undefined
; - `remember-stack [stackname :as local]`
;    save the entire stack to the `local`
;    raise an Exception if it's undefined
; - `place [stackname args kwd]`
;    push the local `kwd` on the named stack
;    raise an Exception if it's undefined
; - `replace-stack [stackname new-stack]`
;    replace the indicated stack with a new list
;    raise an exception if it's undefined


; restrictions are your friend:
; every time a `consume` appears, we know one more item is needed from that stack
; every time `consume-stack` or `count-of` appears, we know we need that type
; every time `remember stackname :as` happens, we know we need at least 1 item
; etc ... 
; In other words, the :needs can be inferred for each instruction from its DSL definition


;; And I THINK that supports all defined Push instructions I've ever seen....


(def six-ints (make-interpreter :stacks {:integer '(6 5 4 3 2 1)}))


(defn store-local
  "stores a local kv pair"
  [[interpreter locals] k v]
  (into [] (list interpreter (assoc locals k v))))


(fact "store-local saves thing to the locals hashmap"
  (second (store-local [(make-interpreter) {}] :foo 8)) => {:foo 8}
  (let [foo (store-local [(make-interpreter) {}] :foo 8)]
    (second (store-local foo :bar 9)) => {:foo 8, :bar 9}))


(fact "store-local can overwrite an existing :locals value"
  (let [foo (store-local [(make-interpreter) {}] :foo 8)]
    (second (store-local foo :foo 999)) => {:foo 999}))


(defn consume
  [[interpreter locals] stack & {:keys [as]}]
  (let [old-stack (get-stack interpreter stack)]
    (cond (empty? old-stack)
            (throw (Exception. (str "Push DSL Error: " stack " is empty")))
          (nil? as)
            [(set-stack interpreter stack (pop old-stack)) locals]
          :else
            (store-local
              [(set-stack interpreter stack (pop old-stack)) locals]
              as
              (first old-stack)))))


(defn get-stack-from-dslblob ;; convenience for testing only
  [[interpreter locals] stackname]
  (get-stack interpreter stackname))


(fact "consume"
  (second (consume [six-ints {}] :integer :as :int1)) => {:int1 6}
  (get-stack-from-dslblob
    (consume [six-ints {}] :integer :as :int1)
    :integer) => '(5 4 3 2 1))


(fact "consume throws an exception if the stack is empty"
  (consume [six-ints {}] :boolean :as :nope) => (throws #"Push DSL Error:"))


(fact "consume can be thread-firsted"
  (let [two-popped (->  [six-ints {}]
                        (consume :integer :as :int1)
                        (consume :integer :as :int2))]
    (second two-popped) => {:int1 6, :int2 5}
    (get-stack-from-dslblob two-popped :integer) => '(4 3 2 1)))


(fact "consume with no :as argument just throws the thing away"
  (let [two-popped (->  [six-ints {}]
                        (consume :integer)
                        (consume :integer :as :int2))]
    (second two-popped) => {:int2 5}
    (get-stack-from-dslblob two-popped :integer) => '(4 3 2 1)))



(defn place
  "pushes a named local onto a given stack"
  [[interpreter locals] stack kwd]
  (let [item (kwd locals)
        updated (push-item interpreter stack item)]
  (into [] (list updated locals))))


(fact "`place` will push the saved local value to the indicated stack in the interpreter"
  (let [integer-added (-> [six-ints {}]
                        (consume :integer :as :int1)
                        (consume :integer :as :int2)
                        (place :boolean :int1))]      
    (get-stack-from-dslblob integer-added :boolean) => '(6)))



;; counting arguments

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
;     (calculate [:how-many :int1] #(mod %1 %2) :as :which)
;     (remember :float :at :which :as :float1)
;     (place :float (recall :float1))]
;     )


; (def-pushinstruction
;   exec-noop
;   :doc "Does nothing."
;   :tags [:core]
;   ;; everything else is default behavior
;   )