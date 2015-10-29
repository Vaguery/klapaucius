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
  :implements #{:numeric :comparable :scalar :arithmetic :combinators :constructible :visible :random}
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
  :implements #{:visible :numeric :compound :arithmetic :combinators :constructible :random}
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
  :implements #{:visible :setlike :compound :combinators :constructible :random :nestable}
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

### Some predefined groups of types and instructions

~~~clojure

;; always present (and can't be removed):
;; :exec :input :log :warning

(use '[push.interpreter.types.core])
;; adds all of :boolean :char :code :integer :float :string 

(use '[push.interpreter.types.core.boolean])
;; adds just :boolean

(use '[push.interpreter.types.stdlib.set])
;; adds :set

(use-explicit '[push.interpreter.instructions.core])
;; adds only the explicitly defined type-specific instructions from each type
;; :boolean-and  <= yes
;; :boolean-pop  <= no    (:combinator generator)
;; :code-points  <= yes
;; :code-to-integer <= no (:converter generator)
;; :float-gt  <= no       (:comparable generator)
;; :float-add  <= no      (:arithmetic generator)
;; :float-divide <= yes   (explicitly overridden in type definition)
;; :boolean-count <= no   (:visible generator)
;; :vector-of-boolean-size <= no (not part of .core)
~~~

## Consistency and other checking

- are the types in all `:needs` for all instructions loaded?
- are the types in all `:makes` for all instructions loaded?
- are all types mentioned in `:prototype` statements mentioned?
- are all types reachable by `construct` methods from all others?
- manifest of each type, including
  - its version
  - how it was loaded (from :core, :stdlib, local file, inline)
- manifest of each instruction loaded, including
  - its version
  - how it was loaded (from :core, :stdlib, local file, inline)


For example, suppose a simple experiment specifies:
1. use `:integer` type
1. load `:integer` instructions from `.core`


## Configuring types and instructions

~~~clojure
(def x (make-interpreter)) ;; the thing we're setting up

(load-into x '[push.interpreter.types.core])
  ;; adds all of :boolean :char :code :integer :float :string
(load-into x '[push.interpreter.types.stdlib.complex])
  ;; adds :complex-int :complex-float
(load-into x '[push.interpreter.types.stdlib.set])
  ;; adds :set
(load-into x '[push.my-project.types.foo])
  ;; adds :foo type from file

(type-manifest x)
;; => :boolean :char :code :complex-float :complex-int :foo :integer :float :set :string
~~~

`(instruction-manifest x)`

~~~text
- :boolean
  - EXPLICIT
    - :boolean-and
    - :boolean-or
    - ...
  - GENERATED
    - :boolean-eq
    - :boolean-pop
    - ...
- :char
  - EXPLICIT
    - [char-specific instructions]
  - GENERATED
    - :char-eq
    - :char-pop
    - ...
- :code
  - EXPLICIT
    - :code-do*range
    - ...
  - GENERATED
    - ...
- :complex-float
  - EXPLICIT
    - ...
  - GENERATED
    - ...
- :complex-int
  - EXPLICIT
    - ...
  - GENERATED
    - ...
- :foo
  - EXPLICIT
    - ...
  - GENERATED
    - ...
- :integer
  - EXPLICIT
    - ...
  - GENERATED
    - ...
- :float
  - EXPLICIT
    - ...
  - GENERATED
    - ...
- :set
  - EXPLICIT
    - ...
  - GENERATED
    - ...
- :string
  - EXPLICIT
    - ...
  - GENERATED
    - ...
~~~