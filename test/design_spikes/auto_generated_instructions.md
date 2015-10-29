#some code sketches for interfaces I need to build:

## types

### An example of a simple Clojure type brought into Push

~~~clojure
(defn right-sized-for-push?
  [number interpreter]
  (<= (abs number) (:integer-size-cutoff interpreter))

(defn push-integer?
  [item]
  (and (integer? item) (right-sized-for-push? item)))

(def-pushtype
  :integer
  :implements #{:numeric :comparable :scalar :arithmetic :combinators :constructible :visible}
  :checker push-integer?)

(construct-comparable-functions :integer
    #(..function..))
; => :integer-lt, :integer-gte, etc


(construct-arithmetic-functions :integer
    #(..check it implements protocols..))
; => :integer-add, :integer-subtract, etc


(construct-combinator-functions :integer)
; => :integer-pop, :integer-swap, etc


(construct-visible-functions :integer)
; => :integer-count, :integer-unique-count


(construct-constructor-functions-into :integer
    [:boolean #(..function..)]
    [:float #(..function..)]
    [:string #(..function..)]
    ...)
~~~

### Complex (-float) numbers in Push

~~~clojure
(def-pushtype
  :complex-float
  :implements #{:visible :numeric :compound :arithmetic :combinators :constructible}
  :prototype {:real :float :imaginary :float}
  :checker push-complex?)


;; build :complex-float arithmetic functions by hand here
(build-instruction
  complex-float-add
  "docstring"
  (..transaction steps..))

(construct-combinator-functions :complex-float)
; => :complex-float-pop, :complex-float-swap, etc


(construct-visible-functions :complex-float)
; => :complex-float-count, :complex-float-count


(construct-constructor-functions-into :complex-float
    [:integer #(..function..)])
; This is based on the :prototype, using :float values as specified
~~~

### A type that doesn't participate in search, can't be popped or read

~~~clojure
(def-pushtype
  :warning
  :implements #{:visible}
  :checker push-warning?)

(construct-visible-functions :complex-float)
; => warning-count, :warning-unique-count
~~~

### Graphs in Push

~~~clojure
(def-pushtype
  :graph
  :implements #{:visible :setlike :compound :combinators :constructible}
  :prototype {:nodes :set :edges ????} ;; need to work this out
  :checker push-graph?)


(construct-set-functions :graph
    ...)
; => :graph-union, :graph-intersection, :graph-powerset...


(construct-combinator-functions :graph)
; => :graph-pop, :graph-swap, etc


(construct-visible-functions :graph)
; => :graph-count, :graph-count


(construct-constructor-functions-into :graph
    [:set #(..function..)])
; This is based on the :prototype, using :set values as specified
~~~