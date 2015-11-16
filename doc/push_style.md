# The Style of Push

Push has particular ways of approaching data, code, representation and other aspects of problem-solving that are worth noting somewhere.

## Argument order

Because of the way the Push interpreter chops up programs, it's inevitable that arguments will be pushed onto stacks _before_ the instruction is executed that consumes those arguments. So in that sense, Push programs (when written out left-to-right) are "sort of postfix". "Sort of" because the instruction need not immediately follow its arguments, nor do all the arguments need to follow one another without intervening items (including, possibly, other instructions).

But while it's true that a Push _program_ is written in a "sort of postfix" direction, it's not always easy to visualize the inevitable rearrangement of the arguments after they get pushed onto their stacks _from_ the program. First, because the program is placed onto the `:exec` stack in an unexpected order, and then because of the popping-and-pushing of items from `:exec`. This is especially important for functions that are [noncommutative](https://en.wikipedia.org/wiki/Commutative_property), like subtraction or comparison.

Consider this example

~~~text
;; Push program

[2 4 :integer-subtract 5 6 :integer-add :integer-lt]

;; initial setup
;; note: the program has been put onto :exec "backwards",
;;       in the sense that Clojure's `(into '() program)`
;;       would have proceeded item-by-item

:exec    '(2 4 :integer-subtract 5 6 :integer-add :integer-lt)
:integer '()
:boolean '()

;; steps

:exec    '(4 :integer-subtract 5 6 :integer-add :integer-lt)
:integer '(2)
:boolean '()

:exec    '(:integer-subtract 5 6 :integer-add :integer-lt)
:integer '(4 2)
:boolean '()

:exec    '(5 6 :integer-add :integer-lt)
:integer '(-2)
:boolean '()

:exec    '(6 :integer-add :integer-lt)
:integer '(5 -2)
:boolean '()

:exec    '(:integer-add :integer-lt)
:integer '(6 5 -2)
:boolean '()

:exec    '(:integer-lt)
:integer '(11 -2)
:boolean '()

:exec    '()
:integer '(11 -2)
:boolean '(true)
~~~