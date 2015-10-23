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
;; (and some store of scratch vars) would be passed step-to-step.
;;
; + `count-of [stackname :as local]`
;    store the number of items in `stackname` under key `local`
;    raise an Exception if the stack doesn't exist

; + `consume-top-of [stackname]`
;    pop an item (and discard it) from `stackname`
;    raise an Exception if it's empty or undefined

; + `consume-top-of [stackname :as local]`
;    pop an item from `stackname` and store under key `local`
;    raise an Exception if it's empty or undefined

; - `consume-nth [stackname :at where :as local]`
;    delete an item from `stackname` at position `where` and store under key `local`
;    raise an Exception if it's empty or undefined

; + `consume-stack [stackname]`
;    clear the named stack
;    raise an Exception if it's undefined

; + `consume-stack [stackname :as local]`
;    save the entire stack into `local` and clear it
;    raise an Exception if it's undefined

; + `remember-calc [args fn :as local]
;    save the result of applying `fn` to the named `args` under key `local`

; + `remember-top [stackname :as local]`
;    store top item from `stackname` under key `local`
;    raise an Exception if it's empty or undefined

; + `remember-nth [stackname :at where :as local]`
;    store item in `stackname` at position `where` under key `local`
;    raise an Exception if it's empty or undefined

; + `remember-stack [stackname :as local]`
;    save the entire stack to the `local`
;    raise an Exception if it's undefined

; + `place [stackname args kwd]`
;    push the local `kwd` on the named stack
;    raise an Exception if it's undefined

; + `replace-stack [stackname kwd]`
;    replace the indicated stack with the local variable
;    if it's a list, replace the stack; if it's not a list, make it the only item
;    raise an exception if it's undefined

; - `instructions [:as kwd]`
;    save a list of all active instructions in the named local

; - `inputs [:as kwd]`
;    save a list of all known inputs in the named local

; - `counter [:as kwd]`
;    save the current interpreter counter in the named local


(def six-ints (make-interpreter :stacks {:integer '(6 5 4 3 2 1)}))

(defn store-scratch
  "stores a scratch kv pair; convenience function for DSL"
  [[interpreter scratch] k v]
  (vector interpreter (assoc scratch k v)))


(fact "store-scratch saves thing to the scratch hashmap"
  (second (store-scratch [(make-interpreter) {}] :foo 8)) => {:foo 8}
  (let [foo (store-scratch [(make-interpreter) {}] :foo 8)]
    (second (store-scratch foo :bar 9)) => {:foo 8, :bar 9}))


(fact "store-scratch can overwrite an existing :scratch value"
  (let [foo (store-scratch [(make-interpreter) {}] :foo 8)]
    (second (store-scratch foo :foo 999)) => {:foo 999}))


(defn consume-top-of
  [[interpreter scratch] stack & {:keys [as]}]
  (let [old-stack (get-stack interpreter stack)]
    (cond (empty? old-stack)
            (throw (Exception. (str "Push DSL Error: " stack " is empty")))
          (nil? as)
            [(set-stack interpreter stack (pop old-stack)) scratch]
          :else
            (store-scratch
              [(set-stack interpreter stack (pop old-stack)) scratch]
              as
              (first old-stack)))))


(defn get-stack-from-dslblob ;; convenience for testing only
  [[interpreter scratch] stackname]
  (get-stack interpreter stackname))


(fact "consume-top-of"
  (second (consume-top-of [six-ints {}] :integer :as :int1)) => {:int1 6}
  (get-stack-from-dslblob
    (consume-top-of [six-ints {}] :integer :as :int1)
    :integer) => '(5 4 3 2 1))


(fact "consume-top-of throws an exception if the stack is empty"
  (consume-top-of [six-ints {}] :boolean :as :nope) => (throws #"Push DSL Error:"))


(fact "consume-top-of can be thread-firsted"
  (let [two-popped (->  [six-ints {}]
                        (consume-top-of :integer :as :int1)
                        (consume-top-of :integer :as :int2))]
    (second two-popped) => {:int1 6, :int2 5}
    (get-stack-from-dslblob two-popped :integer) => '(4 3 2 1)))


(fact "consume-top-of with no :as argument just throws the thing away"
  (let [two-popped (->  [six-ints {}]
                        (consume-top-of :integer)
                        (consume-top-of :integer :as :int2))]
    (second two-popped) => {:int2 5}
    (get-stack-from-dslblob two-popped :integer) => '(4 3 2 1)))



(defn place
  "pushes a named scratch value onto a given stack"
  [[interpreter scratch] stack kwd]
  (let [item (kwd scratch)
        updated (push-item interpreter stack item)]
  (vector updated scratch)))


(fact "`place` will push the saved scratch value to the indicated stack in the interpreter"
  (let [integer-added (-> [six-ints {}]
                          (consume-top-of :integer :as :int1)
                          (consume-top-of :integer :as :int2)
                          (place :boolean :int1))]      
    (get-stack-from-dslblob integer-added :boolean) => '(6)))


(defn remember-calc
  [[interpreter scratch] args function & {:keys [as]}]
  (if (nil? as)
    (vector interpreter scratch)
    (let [result (apply function (map scratch args))]
      (vector interpreter (assoc scratch as result)))))


(fact "remember-calc stores the result of a calculation in a named scratch"
  (let [integer-added (-> [six-ints {}]
                          (consume-top-of :integer :as :int1)
                          (consume-top-of :integer :as :int2)
                          (remember-calc [:int1 :int2] #(+ %1 %2) :as :sum))]
  (second integer-added) => {:int1 6, :int2 5, :sum 11}))


(fact "remember-calc has no effect if :as is not specified"
  (let [integer-added (-> [six-ints {}]
                        (consume-top-of :integer :as :int1)
                        (consume-top-of :integer :as :int2)
                        (remember-calc [:int1 :int2] #(+ %1 %2)))]
  (second integer-added) => {:int1 6, :int2 5}))


(defn remember-top
  [[interpreter scratch] stackname & {:keys [as]}]
  (if (nil? as)
    (vector interpreter scratch)
    (let [result (first (get-stack interpreter stackname))]
      (vector interpreter (assoc scratch as result)))))


(fact "remember-top stores the top item of a named stack"
  (let [integer-munged (-> [six-ints {}]
                          (consume-top-of :integer :as :int1)
                          (consume-top-of :integer :as :int2)
                          (remember-top :integer :as :third))]
  (second integer-munged) => {:int1 6, :int2 5, :third 4}))


(fact "remember-top has no effect if :as is not specified"
  (let [integer-munged (-> [six-ints {}]
                        (consume-top-of :integer :as :int1)
                        (consume-top-of :integer :as :int2)
                        (remember-top :integer))]
  (second integer-munged) => {:int1 6, :int2 5}))


(defn remember-nth
  [[interpreter scratch] stackname & {:keys [as at]}]
  (if (nil? as)
    (vector interpreter scratch)
    (let [stack (get-stack interpreter stackname)
          idx (if (keyword? at)
                    (at scratch)
                    (or at 0))
          which (mod idx (count stack))
          result (nth stack which)]
      (vector interpreter (assoc scratch as result)))))


(fact "remember-nth stores the nth item of a named stack"
  (let [integer-munged (-> [six-ints {}]
                          (consume-top-of :integer :as :int1)
                          (consume-top-of :integer :as :int2)
                          (remember-nth :integer :as :third :at 1))]
  (second integer-munged) => {:int1 6, :int2 5, :third 3}))  ;; 4 *3* 2 1


(fact "remember-nth stores the nth item of a named stack MODULO its actual length"
  (let [integer-munged (->  [six-ints {}]
                            (remember-nth :integer :at 99 :as :99th )
                            (remember-nth :integer :at -8 :as :minus8th))]
  (second integer-munged) => {:99th 3, :minus8th 2}
  (get-stack-from-dslblob integer-munged :integer) => '(6 5 4 3 2 1)
  (mod 99 6) => 3   ;; 6 5 4 *3* 2 1
  (mod -8 6) => 4)) ;; 6 5 4 3 *2* 1



(fact "remember-nth has no effect if :as is not specified"
  (let [integer-munged (-> [six-ints {}]
                        (consume-top-of :integer :as :int1)
                        (consume-top-of :integer :as :int2)
                        (remember-nth :integer))]
  (second integer-munged) => {:int1 6, :int2 5}))


(fact "remember-nth acts like remember-top if :at is not specified"
  (let [integer-munged (-> [six-ints {}]
                        (consume-top-of :integer :as :int1)
                        (consume-top-of :integer :as :int2)
                        (remember-nth :integer :as :adsa))]
  (second integer-munged) => {:int1 6, :int2 5, :adsa 4}))


(fact "remember-nth will use a scratch item for its index if one is given"
  (let [integer-munged (-> [six-ints {}]
                        (remember-nth :integer :at 2 :as :int1) ;; 4
                        (remember-nth :integer :at :int1 :as :maybe2))]
  (second integer-munged) => {:int1 4, :maybe2 2}))



(defn remember-stack
  [[interpreter scratch] stackname & {:keys [as at]}]
  (if (nil? as)
    (vector interpreter scratch)
    (let [stack (get-stack interpreter stackname)]
      (vector interpreter (assoc scratch as stack)))))

(fact "remember-stack stores the stack specified in the :as variable"
  (let [integer-munged (-> [six-ints {}]
                          (consume-top-of :integer :as :int1)
                          (consume-top-of :integer :as :int2)
                          (remember-stack :integer :as :rest))]
  (second integer-munged) => {:int1 6, :int2 5, :rest '(4 3 2 1)}))


(fact "remember-stack has no effect if :as is not specified"
  (let [integer-munged (-> [six-ints {}]
                        (consume-top-of :integer :as :int1)
                        (consume-top-of :integer :as :int2)
                        (remember-stack :integer))]
  (second integer-munged) => {:int1 6, :int2 5}))


(defn replace-stack
  [[interpreter scratch] stackname kwd]
  (let [new-val (kwd scratch)
        new-stack (if (list? new-val) new-val (list new-val))
        result (set-stack interpreter stackname new-stack)]
      (vector result scratch)))


(fact "replace-stack replaces the entire stack with the named value from scratch"
  (let [totally-made-up (-> [six-ints {}]
                            (remember-calc [] #(list 1 2 3 5 8) :as :fibs)
                            (replace-stack :integer :fibs))]
  (get-stack-from-dslblob totally-made-up :integer) => '(1 2 3 5 8)))


(fact "replace-stack replaces the entire stack with the item in a list"
  (let [integer-munged (-> [six-ints {}]
                          (consume-top-of :integer :as :int1)
                          (replace-stack :integer :int1))]
  (get-stack-from-dslblob integer-munged :integer) => '(6)))


(defn count-of
  [[interpreter scratch] stackname & {:keys [as]}]
  (if (nil? as)
    (vector interpreter scratch)
    (let [howmany (count (get-stack interpreter stackname))]
      (vector interpreter (assoc scratch as howmany)))))

(fact "count-of saves the size of the named stack"
  (let [tester (-> [six-ints {}]
                   (count-of :integer :as :ints))]
    (second tester) => {:ints 6}))

(fact "count-of does nothing if no local is specified"
  (let [tester (-> [six-ints {}]
                   (count-of :integer))]
    (second tester) => {}))


(defn consume-stack
  [[interpreter scratch] stack & {:keys [as]}]
  (let [old-stack (get-stack interpreter stack)]
    (cond (nil? as)
            [(set-stack interpreter stack (list)) scratch]
          :else
            (store-scratch
              [(set-stack interpreter stack (list)) scratch]
              as
              old-stack))))

(fact "consume-stack"
  (second (consume-stack [six-ints {}] :integer :as :ints)) => {:ints '(6 5 4 3 2 1)}
  (get-stack-from-dslblob
    (consume-stack [six-ints {}] :integer :as :int1)
    :integer) => '())


(fact "consume-stack with no :as argument just throws the thing away"
  (let [gone (->  [six-ints {}]
                  (consume-stack :integer))]
    (second gone) => {}
    (get-stack-from-dslblob gone :integer) => '()))


;;; LET'S MAKE AN INSTRUCTION FROM THIS CRAP

;; first let's take a transaction and make it into a real function

(defmacro
  def-pushdsl
  [& transactions]
  (let [interpreter (gensym 'interpreter)]
    `(fn [~interpreter] 
      (first (-> [~interpreter {}] ~@transactions)))))

(fact "that works maybe"
  (fn?
    (macroexpand-1
      (def-pushdsl
        (remember-calc [] #(list 1 2 3 5 8) :as :fibs)
        (replace-stack :integer :fibs)))) => true)

;; integer_add

(def int-adder 
  (def-pushdsl
    (consume-top-of :integer :as :int1)
    (consume-top-of :integer :as :int2)
    (remember-calc [:int1 :int2] #(+ %1 %2) :as :sum)
    (place :integer :sum)))

(fact "that int-adder thing actually does the thing"
  (get-stack (int-adder six-ints) :integer) => '(11 4 3 2 1))


;; boolean_flush

(def bool-flusher 
  (def-pushdsl
    (consume-stack :boolean)))

(fact "that bool-flusher thing actually does the thing"
  (get-stack
    (bool-flusher (make-interpreter :stacks {:boolean '(false true false)}))
    :boolean) => '())

;; float_yankdup 

(def float-yankduper 
  (def-pushdsl
    (consume-top-of :integer :as :index)
    (count-of :float :as :how-many)
    (remember-calc [:index :how-many] #(mod %1 %2) :as :which)
    (remember-nth :float :at :which :as :dup-me)
    (place :float :dup-me)
    ))

(fact "that float-yankduper thing actually does the thing"
  (get-stack
    (float-yankduper
      (make-interpreter :stacks {:float '(1.1 2.2 3.3) :integer '(4)}))
      :float) => '(2.2 1.1 2.2 3.3))


;; exec-noop

(def exec-noop 
  (def-pushdsl))

(def boring (make-interpreter))

(fact "that noop thing actually does not one thing"
  (exec-noop boring) => boring)
