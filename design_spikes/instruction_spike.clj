;; This is a design spike. That means it's NOT TO BE USED.

;; Thinking about each Push instruction as a "transaction" involving a single Interpreter
;; passing in and out.

;; This would be threaded steps. Probably a macro DSL.
;; - everything would be assumed to happen inside the interpreter context
;; - count-of would return the number of items of the named stack
;; - consume-as would pop an item from the named stack and assign it to a (local) symbol
;; - consume-nth-as would extract a numbered item from the stack and record it
;; - consume-stack-as would grab the entire list as a single (local) symbol
;; - record-as would save the current top item on the named stack
;; - record-nth-as would save the nth item on the named stack
;; - discard would pop an item from the named stack
;; - discard-stack would empty the named stack
;; - send-to would push the indicated value to that stack
;; - replace-stack would replace the indicated stack with a new list

{
:integer_add [
  #(consume-as % int1 :integer)
  #(consume-as % int2 :integer)
  #(send-to % :integer (+ int1 int2))]

:boolean_flush [
  #(discard-stack % :boolean)]

:float_yankdup [
  #(consume-as % int1 :integer)
  #(record-nth-as % float1 :float (mod int1 (count-of :float)))
  #(send-to % :float float1)]
}


;; the more complete instruction creator would be something like

(def-pushinstruction
  integer-add
  :doc "adds two :integers"
  :needs {:integer 2}
  :makes {:integer 1}
  :tags [:arithmetic :core]
    (consume-as int1 :integer)
    (consume-as int2 :integer)
    (send-to :integer (+ int1 int2))
    )


(def-pushinstruction
  boolean-flush
  :doc "empties the :boolean stack"
  :needs {:boolean 0}
  :tags [:combinator :core]
  :transaction
    (discard-stack :boolean)
    )


(def-pushinstruction
  float-yankdup
  :doc "Takes an :integer, and copies the indicated nth item (modulo the :float stack size) on the :float stack to the top; so if the :integer is 12 and the :float stack has 5 items, the (mod 12 5) item is copied to the top as a new 6th item."
  :needs {:integer 1 :float 1}
  :tags [:core :combinator]
  :transaction
    (consume-as int1 :integer)
    (record-nth-as float1 :float (mod int1 (count-of :float)))
    (send-to :float float1)]
    )


(def-pushinstruction
  exec-noop
  :doc "Does nothing."
  :tags [:core]
  ;; everything else is default behavior
  )