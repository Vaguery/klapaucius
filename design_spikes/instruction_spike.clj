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
  #(send-to % :float record-nth-as)]
}