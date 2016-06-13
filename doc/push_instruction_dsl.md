# The Push Instruction DSL

The Push Instruction DSL is a highly-constrained domain-specific language for defining Push instructions.

## What a Push instruction does

Every Push instruction takes an entire `Interpreter` as its argument, and returns a modified `Interpreter` as its result. The transformation of the `Interpreter` state from input to output is composed of a series of imperative transformations in the DSL, using ephemeral state variables saved in a "scratch map" in order to pass intermediate results between steps of processing.

Let me walk through a few examples of simple and complex instruction behavior, just to clarify:

- `:integer-divide` pops two `:integer` values as arguments (call them `B` and `A`, respectively); if `B` is zero, it pushes both values back onto the `:integer` stack unchanged, and pushes an item with the value `":integer-divide 0 denominator"` onto the `:error` stack; otherwise, it pushes the integer quotient `A÷B` onto the `:integer` stack
- `:exec-if` pops the top two `:exec` items and the top `:boolean` item; if the latter is `true`, the first `:exec` item is pushed back onto that stack; if `false`, the second one is.
- `:string-cutflip` takes an `:integer` argument, and transforms it into an index over the `:string` stack by taking its value modulo that stack's current size; if the stack is empty, the index is 0. It then takes all items from the `:string` stack down to the indexed item, and reverses their order, leaving the remainder of the stack unchanged.
- `:push-inputs` pushes a list of all the currently defined `input` keywords (in lexicographically sorted order) onto the `:exec` stack

Almost all Push instructions consume arguments from one or more specified stacks. Whenever any argument is missing, everything else is left in place and an `:error` is added to that stack recording the shortage. So for example, `:integer-divide` will not change the values on the `:integer` stack if there is only one number there.

## DSL fundamentals

The Push Instruction DSL is a very constrained subset of Clojure. While the definitions of some instructions take more characters than you might normally type in an _ad hoc_ program, by requiring instructions to be written in the DSL we are also able to:

- automatically infer the stack types and minimum number of items that must appear on those stacks for an instruction to work
- simplify and optimize coded definitions in the background
- greatly simplify unit testing for common but frequently-missed edge cases (_e.g._, bad arguments)
- monitor and summarize interactions and similarities between registered Instructions from their source code, simplifying the design, implementation, and management of new types, instructions and experiments
- support validation of complex instructions, aspects of Push functionality, and Push type definitions
- configure and customize the `Interpreter`'s behavior in response to "missing instructions" or "dynamically redefined instructions" automatically
- change Interpreter behavior globally without changing the code of each Instruction
- improve maintainability of the codebase
- simplify logging and debugging information with globally-defined side-effects
- automate the generation of "standard" instructions for new user-defined types

As I said, there are some constraints on the types of things that can happen within an instruction definition, in one transaction step:

- one item can be popped from a stack, stored in a scratch variable
- an entire stack can be moved into a scratch variable (deleting them)
- one item or stack can be deleted
- one item or stack can be saved into a scratch variable without being deleted
- arbitrary Clojure functions can be invoked, but can _only_ use named scratch variables or inline literals as their arguments; their results are saved in other scratch variables
- one or several items can be pushed to a single stack
- one item can be placed into an arbitrary position in a stack
- a stack can be replaced with the contents of a scratch variable
- a number of `Interpreter` state and setting variables can be interrogated (number of steps, step limit, etc)

Thus, the overall structure of every Push instruction follows the same general blueprint:

1. consume arguments (items, entire stacks, or state information), and save them to scratch variables
2. calculate the correct result(s), saving them to scratch variables
3. place the result(s) from scratch storage onto the appropriate stack(s), one stack at a time

While one instruction may consume arguments and another may simply "read" them, all of the standard Push 3.0 and Clojush instructions have be built on this simplified framework.

## A couple of (working) examples

These are calls to the `build-instruction` macro, which creates a new `Instruction` record with various other settings besides the transaction itself. The last few lines of each (after the `:tags` line) are the transaction proper.

`:boolean->integer`:

~~~clojure
(def boolean->integer
  (core/build-instruction
    boolean->integer
    "`:boolean->integer` pops the top `:boolean`. If it's `true`, it pushes 1; if `false`, it pushes 0."
    :tags #{:base :conversion}
    (d/consume-top-of :boolean :as :arg1)
    (d/calculate [:arg1] #(if %1 1 0) :as :logic)
    (d/push-onto :scalar :logic)))
~~~

`:code-do*range`:

~~~clojure
(def code-do*range
  (core/build-instruction
    code-do*range
    "`:code-do*range` pops the top item of `:code` and the top two `:integer` values (call them `end` and `start`, respectively, with `end` being the top `:integer` item). It constructs a continuation depending on the relation between the `end` and `start` values:

      - `end` > `start`: `'([start] [code] ([start+1] [end] :code-quote [code] :code-do*Range))`
      - `end` < `start`: `'([start] [code] ([start-1] [end] :code-quote [code] :code-do*Range))`
      - `end` = `start`: `'(end [code])`

    This continuation is pushed to the `:exec` stack."
    :tags #{:complex :base}
    (d/consume-top-of :code :as :do-this)
    (d/consume-top-of :integer :as :end)
    (d/consume-top-of :integer :as :start)
    (d/calculate [:start :end] #(= %1 %2) :as :done?)
    (d/calculate [:start :end] #(+ %1 (compare %2 %1)) :as :next)
    (d/calculate
      [:do-this :start :end :next :done?] 
      #(if %5
           (list %3 %1)
           (list %2 %1 (list %4 %3 :code-quote %1 :code-do*range))) :as :continuation)
    (d/push-onto :exec :continuation)))
~~~

## DSL scratch variables

All scratch variables are referred to by Clojure keywords (not symbols). These are keys of a transient local store, and you shouldn't have to worry about namespace leakage because the store will be discarded as soon as the instruction transaction is complete. The store is a simple `hash-map`, so be aware that saving a new value to an already-defined key will overwrite the old value.

## DSL commands

- [X] `calculate [args fn :as local]`
  
  Example: `(calculate [:int1 :int2] #(+ %1 %2) :as :sum)`

  `args` is a vector of keywords, referring to scratch variables

  `fn` is an arbitrary Clojure inline function which _must_ refer to its arguments positionally as listed in the `args` vector; the function can of course invoke Clojure symbols defined outside the instruction, and can contain literals, but should not refer to any _arguments_ other than those listed in that vector.

  The result of invoking `fn` on `args` is saved into scratch variable `local`, overwriting any previous value.

- [X] `consume-nth-of [stackname :at where :as local]`

  Example: `consume-nth-of :boolean :at 3 :as :bool3]`

  Example: `consume-nth-of :boolean :at :middle :as :bool3]`
  
  Removes the item in position `where` from the named stack, and stores it in scratch variable `local`.

  If `where` is an integer literal, then the index of the item removed is `(mod where (count stackname))`. If `where` is a keyword, then the index is obtained by looking it up in the scratch storage.

  If the index (obtained via a local scratch value) is not an integer (including `nil`), an Exception is raised. If it is an integer, it is treated as before, and `mod`ded into the appropriate range.

  The relative order of `:at` and `:as` arguments are not crucial, except for readability, but both must be present.
    
- [X] `consume-stack [stackname :as local]`

  Example: `consume-stack :code as :old-code`

  Saves the entire named stack into `local` and clears it in the `Interpreter`.

- [X] `consume-top-of [stackname :as local]`

  Example: `consume-top-of :float as :numerator`
  
  Pops an item from `stackname` and stores under key `local`. Raises an Exception if `stackname` is empty or undefined. Will overwrite any prior value stored in `local`.

- [X] `count-of [stackname :as local]`

  Example: `count-of :float :as :float-size`

  Stores the number of items in stack `stackname` in scratch variable `local`.

- [X] `delete-nth-of [stackname :at where]`
  
  Example: `delete-nth-of :integer :at -19`

  Example: `delete-nth-of :integer :at :bad-number`

  Removes the item in position `where` from the named stack without storing it.

  If `where` is an integer literal, then the index of the item removed is `(mod where (count stackname))`. If `where` is a keyword, then the index is obtained by looking it up in the scratch storage.

  If the index (obtained via a local scratch value) is not an integer (including `nil`), an Exception is raised. If it is an integer, it is treated as before, and `mod`ded into the appropriate range.

- [X] `delete-top-of [stackname]`
  
  Example: `delete-top-of :float`

  Pop an item (and discards it) from `stackname`.

  Raises an Exception if `stackname` is empty or undefined.

- [X] `delete-stack [stackname]`

  Example: `delete-stack :vector_of_boolean`

  Empty the named stack in the `Interpreter`.

- [X] `insert-as-nth-of [stackname :local :at where]`
  
  Example: `insert-as-nth-of :integer :new-int :at -19`

  Example: `insert-as-nth-of :boolean :new-bool :at :index`

  Inserts the indicated item from `scratch` so that it is now in position `where` in the named stack.

  If `where` is an integer literal, then the index of the item inserted is `(mod where (count stackname))`. If `where` is a keyword, then the index is obtained by looking that up in the scratch storage.

  If the index (obtained via a local scratch value) is not an integer (including `nil`), an Exception is raised. If it is an integer, it is treated as before, and `mod`ded into the appropriate range.

- [X] `push-onto [stackname local]`

  Example: `push-onto :scalar :sum`

  Push the scratch value `local` onto the top of the indicated stack. If the `local` is `nil`, that's OK; nothing bad will happen.

- [X] `push-these-onto [stackname [locals]]`

  Example: `push-these-onto :integer [:quotient :remainder]`

  Push _each_ of the indicated scratch values in the _vector_ onto the top of the indicated stack, one at a time, first one pushed first, last one pushed last (and thus ending up at the top). If any of them is `nil` that's OK; nothing bad will happen.

- [X] `save-counter [:as local]`
  
  Example: `save-counter :as :steps`

  Saves the current interpreter counter value in `local`.

- [X] `save-bindings [:as local]`

  Example: `save-bindings :as :all-variables`

  Saves a Clojure `set` containing all registered bindings names in scratch variable `local`.

- [X] `save-instructions [:as local]`

  Example: `save-instructions :as :registered`

  Builds a Clojure `set` of the keywords used to invoke Push instructions in this (running) `Interpreter`, and saves it in the scratch variable `local`.

- [X] `record-an-error [:from local]`
  
  Example: `record-an-error :from :my-bad-message`

  Constructs a map with keys `:step` and `:item`, the former set to the current `Interpreter` counter, and the latter set to the value stored in the named scratch variable. This is pushed to the `:error` stack. For example, this is used to produce a record of divide-by-zero errors when `:integer-divide` is given a zero denominator.

- [X] `save-nth-of [stackname :at where :as local]`
  
  Example: `save-nth-of :boolean :at 7 :as :seventh`

  Example: `save-nth-of :boolean :at :best-one :as :best-val`

  Copies the item in position `where` into the scratch variable `local`.

  If `where` is an integer literal, then the index of the item removed is `(mod where (count stackname))`. If `where` is a keyword, then the index is obtained by looking it up in the scratch storage.

  If the index (obtained via a local scratch value) is not an integer (including `nil`), an Exception is raised. If it is an integer, it is treated as before, and `mod`ded into the appropriate range.

  The relative order of `:at` and `:as` arguments are not crucial, except for readability, but both must be present.

- [X] `save-top-of [stackname :as local]`

  Example: `save-top-of :exec :as :next-item`

  Copies the top item from `stackname` into scratch variable `local` without removing it from the stack.

- [X] `save-stack [stackname :as local]`

  Example: `save-stack :float :as :unsorted`

  Saves a copy of the entire list `stackname` into scratch variable `local`. Does not clear the stack.

- [X] `replace-stack [stackname local]`
  
  Example: `replace-stack :integer :converted-floats`

  Replace the indicated stack with the contents of the scratch variable `local`.

  If the stored value is `nil`, empty the stack; if it is a list, _replace_ the stack with that list; if it is not a list, replace the stack with a new list _containing only that one value_. For example, if the stored value is `'(1 2 3)` then the new stack will be `'(1 2 3)`; if the stored value is `[1 2 3[`, the new stack will be `'([1 2 3])`.

## Argument checking

Each of the Push Instruction DSL instructions has a strict structure that helps identify arguments consumed by the stacks from which they're taken. As a result, every `Instruction` record has an automatically-constructed `:needs` key, which contains a tally of the items needed to execute the defined DSL code. Popping an argument from a stack counts as one item needed; saving or deleting a stack (or pushing an item onto it) simply requires the stack be present.

For example, the `:needs` of the two instructions shown above are:

- `:boolean->integer`
  - `(d/consume-top-of :boolean :as :arg1)` needs `{:boolean 1}`
  - `(d/calculate [:arg1] #(if %1 1 0) :as :logic)` needs `{}`
  - `(d/push-onto :scalar :logic)` needs `{:integer 0}`
  - total needs: `{:boolean 1 :integer 0}`
- `:code-do*range`
  - `(d/consume-top-of :code :as :do-this)` needs `{:code 1}`
  - `(d/consume-top-of :integer :as :end)` needs `{:integer 1}`
  - `(d/consume-top-of :integer :as :start)` needs `{:integer 1}`
  - `(d/calculate…)` needs `{}`
  - `(d/calculate…)` needs `{}`
  - `(d/calculate…)` needs `{}`
  - `(d/push-onto :exec :continuation)`  needs `{:exec 0}`
  - total needs: `{:integer 2 :code 1 :exec 0}`

Whenever any Push instruction is executed, the `Interpreter` first checks to see whether the specified `:needs` are currently present in the stacks. If they are, the instruction proceeds; if not, the instruction will fail, and (depending on configuration) an `:error` will be pushed that that stack indicating arguments were missing at this time-step.

### Mapping the Push language

As noted above, Push instructions don't "return results", but rather affect the `Interpreter` state directly, sometimes in subtle ways. But the majority do produce items pushed to stacks, and we can as easily calculate the `:products` of a Push instruction from its source code as we can its `:needs`. 

[TBD: diagram of the instructions, stacks and so forth]