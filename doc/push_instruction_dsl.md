# The Push Instruction DSL

The Push Instruction DSL is a highly-constrained domain-specific language for defining Push instructions.

## What a Push instruction does

Every Push instruction takes an entire `Interpreter` as its argument, and returns a modified `Interpreter` as its result. The transformation of the `Interpreter` state from input to output is composed of a series of imperative transformations in the DSL, conducted as a single uninterruptible _transaction_ and using ephemeral state variables saved in a "scratch map" in order to pass intermediate results between steps of processing.

Let me walk through a few examples of simple and complex instruction behavior, just to clarify:

- `:scalar-divide` pops two `:scalar` values as arguments (call them `B` and `A`, respectively); if `B` is not zero, then it calculates the quotient `A÷B` and pushes that onto the `:scalar` stack. If `B` is zero, then it pushes an `:error` item with the message `":scalar-divide 0 denominator"` onto the `:error` stack
- `:exec-if` pops the top two `:exec` items and the top `:boolean` item; if the latter is `true`, the first `:exec` item is pushed back onto that stack; if `false`, the second one is.
- `:string-cutflip` takes a `:scalar` argument, and transforms that number into an index over the `:string` stack by taking its value modulo that stack's current size; if the stack is empty, the index is 0. It then reverses the order of all items on the `:string` stack from the top down to the index position, leaving the remainder of the stack unchanged.
- `:push-bindings` pushes a list of all the currently defined `input` keywords (in lexicographically sorted order) onto the `:exec` stack

As you can see, there's a lot going on inside many of the standard Push instructions. Not only are arguments popped from stacks, but branching calculations can occur, outcomes may change depending on arguments' or intermediate calculations' values, and so on. The Push DSL exists to _manage_ that potential complexity. It intentionally limits the scope of Clojure with the goal of keeping individual instructions---even your domain-specific ones---manageable and maintainable.

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

As I said, there are some constraints on the types of things that can happen within an instruction definition, in one DSL step:

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

While one instruction may consume arguments and another may simply "read" them, all of the standard Push 3.0 and Clojush instructions can be built on this simplified framework.

## A couple of (working) examples

These are calls to the `build-instruction` macro, which creates a new `Instruction` record with various other settings besides the transaction itself. The last few lines of each (after the `:tags` line) are the transaction proper.

`:boolean->signedint`:

~~~clojure
(def boolean->signedint
  (core/build-instruction
    boolean->signedint
    "`:boolean->signedint` pops the top `:boolean`. If it's `true`, it pushes 1; if `false`, it pushes -1."
    :tags #{:base :conversion}
    (push.instructions.dsl/consume-top-of :boolean :as :arg1)
    (push.instructions.dsl/calculate [:arg1] #(if %1 1 -1) :as :logic)
    (push.instructions.dsl/push-onto :scalar :logic)))
~~~

Let's walk through that definition of `:boolean->signedint`. I'm defining the instruction using the `push.instructions.core/build-instruction` macro, which compiles the information that follows into a self-contained `Instruction` Clojure record. The first argument, here `boolean->signedint`, is simply the name of the instruction itself, and it will be the keyword used by a running Interpreter to recognize the instruction. Wherever the keyword `:boolean->signedint` appears in a Push program, an `Interpreter` which "knows" this instruction will immediately execute the following code. The next argument is the documentation string, which explains the way this particular instruction behaves when interpreted. Then there's a keyword-named argument, `:tags`, which helps classify the "family" and behavior of this particular instruction for human readers.

Finally we begin with the steps executed, in sequence, when this instruction is invoked by a running interpreter. Here's what the DSL steps do:

1. `(push.instructions.dsl/consume-top-of :boolean :as :arg1)` This pops the top item from the `:boolean` stack, and stores it in a new "scratch variable" called `:arg1`
2. `(push.instructions.dsl/calculate [:arg1] #(if %1 1 -1) :as :logic)` This slightly convoluted syntax is used in the DSL to permit some simple argument-checking algorithms to be applied to more or less arbitrary Clojure code: The _argument_ of this calculation is explicitly `:arg1`, and the function which follows refers to that argument using a positional placeholder. Finally, the result is stored in a new scratch variable called `:logic`.
3. `(push.instructions.dsl/push-onto :scalar :logic)` This step takes whatever is found in the scratch variable `:logic` and pushes it onto the `:scalar` stack.

A few things to notice about the DSL:

- Push "arguments" (items popped from stacks to be consumed or even referred to within a running instruction) _must_ be stored in scratch variables before being referenced in a calculation or pushed somewhere new
- intermediate calculations _must_ be complete items can be pushed onto stacks
- results of calculations can _only_ be stored in scratch variables
- items to be pushed onto stacks can _only_ be obtained from scratch variables

`:code-do*range`:

~~~clojure
(def code-do*range
  (core/build-instruction
    code-do*range
    "`:code-do*range` pops the top item of `:code` and the top two `:scalar` values (call them `end` and `start`, respectively, with `end` being the top `:scalar` item). It constructs a continuation depending on the relation between the `end` and `start` values:

      - `end` > `(inc start)`: `'([start] [code] ((inc [start]) [end] :code-quote [code] :code-do*Range))`
      - `end` < `(dec start)`: `'([start] [code] ((dec [start]) [end] :code-quote [code] :code-do*Range))`
      - (`end` - `start`) ≤ 1: `'((dec [start]) [code])`

    This continuation is pushed to the `:exec` stack."
    :tags #{:complex :base}
    (d/consume-top-of :code :as :do-this)
    (d/consume-top-of :scalar :as :end)
    (d/consume-top-of :scalar :as :start)
    (d/calculate [:start :end] #(num/within-1? %1 %2) :as :done?)
    (d/calculate [:start :end] #(+' %1 (compare %2 %1)) :as :next)
    (d/calculate
      [:do-this :start :end :next :done?] 
      #(if %5
           (list %4 %1)
           (list %2 %1 (list %4 %3 :code-quote %1 :code-do*range))) :as :continuation)
    (d/push-onto :exec :continuation)))
~~~

Here I've abbreviated the namespace of the DSL instructions as `d`, but it's still a reference (in the original context) to `push.instructions.dsl`. Let's walk through this example's DSL steps, too.

1. `(d/consume-top-of :code :as :do-this)` pops the top `:code` item and stores it in a scratch variable called `:do-this`
2. `(d/consume-top-of :scalar :as :end)` pops the top `:scalar` and stores it in `:end`
3. `(d/consume-top-of :scalar :as :start)` pops the top `:scalar` and stores it in `:start`
4. `(d/calculate [:start :end] #(num/within-1? %1 %2) :as :done?)` We use the numerical values stored in `:start` and `:end`, invoking a predicate function `within-1?` stored in another namespace, to determine whether the looping this instruction is responsible for is "done" or not
5. `(d/calculate [:start :end] #(+' %1 (compare %2 %1)) :as :next)` Regardless of whether we're done or not, we'll want to know the `:next` counter this instruction is supposed to emit
6. ```
  (d/calculate
      [:do-this :start :end :next :done?] 
      #(if %5
           (list %4 %1)
           (list %2 %1 (list %4 %3 :code-quote %1 :code-do*range))) :as :continuation)
  ```
  Here we're using the accumulated intermediate values and calculations to construct an appropriate _continuation_ result
7. `(d/push-onto :exec :continuation)` The continuation result we construct in the previous step is pushed onto `:exec`, and we're done

Again, there are a few things to note here:

- DSL `calculate` steps are able to refer to arbitrary Clojure code indirectly. Whenever you need to do complex calculations (or invoke system libraries or utilities), _as long as you can obtain the necessary arguments in previous steps_ you can invoke those external functions or helpers whenever you want.
- Many of the more important Push instructions, and especially the ones that handle conditional processing, iteration and looping, use "continuation forms" for producing intermediate results. In this case, `:code-do*range` produces a continuation form that (in some cases) includes a new copy of `:code-do*range`. That's how Push interpreters generally should break down long-running calculations of arbitrary complexity.


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

  _Note:_ Top be valid, the index must produce a `true` response to Clojure's `integer?` predicate!

  The relative order of `:at` and `:as` arguments are not crucial, except for readability, but both must be present.
    
- [X] `consume-stack [stackname :as local]`

  Example: `consume-stack :code as :old-code`

  Saves the entire named stack into `local` and clears it in the `Interpreter`.

- [X] `consume-top-of [stackname :as local]`

  Example: `consume-top-of :scalar as :numerator`
  
  Pops an item from `stackname` and stores under key `local`. Raises an Exception if `stackname` is empty or undefined. Will overwrite any prior value stored in `local`.

- [X] `count-of [stackname :as local]`

  Example: `count-of :scalar :as :width`

  Stores the number of items in stack `stackname` in scratch variable `local`.

- [X] `delete-nth-of [stackname :at where]`
  
  Example: `delete-nth-of :string :at -19`

  Example: `delete-nth-of :string :at :bad-number`

  Removes the item in position `where` from the named stack without storing it.

  If `where` is an integer literal, then the index of the item removed is `(mod where (count stackname))`. If `where` is a keyword, then the index is obtained by looking it up in the scratch storage.

  If the index (obtained via a local scratch value) is not an integer (including `nil`), an Exception is raised. If it is an integer, it is treated as before, and `mod`ded into the appropriate range.

- [X] `delete-top-of [stackname]`
  
  Example: `delete-top-of :boolean`

  Pop an item (and discards it) from `stackname`.

  Raises an Exception if `stackname` is empty or undefined.

- [X] `delete-stack [stackname]`

  Example: `delete-stack :vector_of_boolean`

  Empty the named stack in the `Interpreter`.

- [X] `insert-as-nth-of [stackname :local :at where]`
  
  Example: `insert-as-nth-of :vector :new-vector :at -19`

  Example: `insert-as-nth-of :boolean :new-bool :at :index`

  Inserts the indicated item from `scratch` so that it is now in position `where` in the named stack.

  If `where` is an integer literal, then the index of the item inserted is `(mod where (count stackname))`. If `where` is a keyword, then the index is obtained by looking that up in the scratch storage.

  If the index (obtained via a local scratch value) is not an integer (including `nil`), an Exception is raised. If it is an integer, it is treated as before, and `mod`ded into the appropriate range.

- [X] `push-onto [stackname local]`

  Example: `push-onto :scalar :sum`

  Push the scratch value `local` onto the top of the indicated stack. If the `local` is `nil`, that's OK; nothing bad will happen.

- [X] `push-these-onto [stackname [locals]]`

  Example: `push-these-onto :exec [:quotient :remainder]`

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

  Constructs a map with keys `:step` and `:item`, the former set to the current `Interpreter` counter, and the latter set to the value stored in the named scratch variable. This is pushed to the `:error` stack. For example, this is used to produce a record of divide-by-zero errors when `:scalar-divide` is given a zero denominator.

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

  Example: `save-stack :foo :as :unsorted`

  Saves a copy of the entire list `stackname` into scratch variable `local`. Does not clear the stack.

- [X] `replace-stack [stackname local]`
  
  Example: `replace-stack :foo :converted-floats`

  Replace the indicated stack with the contents of the scratch variable `local`.

  If the stored value is `nil`, empty the stack; if it is a list, _replace_ the stack with that list; if it is not a list, replace the stack with a new list _containing only that one value_. For example, if the stored value is `'(1 2 3)` then the new stack will be `'(1 2 3)`; if the stored value is `[1 2 3[`, the new stack will be `'([1 2 3])`.

## Argument checking

Each of the Push Instruction DSL instructions has a strict structure that helps identify arguments consumed by the stacks from which they're taken. As a result, every `Instruction` record has an automatically-constructed `:needs` key, which contains a tally of the items needed to execute the defined DSL code. Popping an argument from a stack counts as one item needed; saving or deleting a stack (or pushing an item onto it) simply requires the stack be present.

For example, the `:needs` of the instruction shown in the example up top (`:boolean->signedint` and `:code-do*range`)

- `:boolean->integer`
    - `(d/consume-top-of :boolean :as :arg1)`
    - `(d/calculate [:arg1] #(if %1 1 -1) :as :logic)`
    - `(d/push-onto :scalar :logic)))`
  - total needs: `{:boolean 1 :scalar 0}`
- `:code-do*range`
  - `(d/consume-top-of :code :as :do-this)` needs `{:code 1}`
  - `(d/consume-top-of :scalar :as :end)` needs `{:scalar 1}`
  - `(d/consume-top-of :scalar :as :start)` needs `{:scalar 1}`
  - `(d/calculate…)` needs `{}`
  - `(d/calculate…)` needs `{}`
  - `(d/calculate…)` needs `{}`
  - `(d/push-onto :exec :continuation)`  needs `{:exec 0}`
  - total needs: `{:scalar 2 :code 1 :exec 0}`

Whenever any Push instruction is executed, the `Interpreter` first checks to see whether the specified `:needs` are currently present in the stacks. If they are, the instruction proceeds; if not, the instruction will fail, and (depending on configuration) an `:error` will be pushed that that stack indicating arguments were missing at this time-step.

## Writing your own instructions using the DSL

The Push DSL is surprisingly complete, especially with the flexibility it provides for using helpers in `calculate`. It may feel stilted in comparison to your own Clojure coding style preferences, but realize that its purpose is to support the automation of several chores you'd be obliged to handle in every instruction you wrote.

- argument-checking is automated
- instructions can be constructed _functionally_ for arbitrary or new types
- any instruction can be used as part of a _higher-order function_

Take a moment to compare the implementation of `:code-do*range` in the Push DSL, with the [same instruction implemented in the Clojush system](https://github.com/lspector/Clojush/blob/c6e9bc5e9835ed3c57703509e5b9fb988ea5f081/src/clojush/instructions/code.clj#L95-L120).

If you glance at that code, you'll see

- arguments are checked right in the instruction function itself
- the predicates used for argument checking are composed of nested arbitrary logic
- most of the function is construction of various local variables in a `let` block
- the `push-item` function includes the convoluted function that constructs its own argument _inline_
- the whole function is essentially wrapped in a conditional `if` statement that _literally does nothing_ if the initial conditions 20+ lines earlier are not met

`/shrug`

So if you find yourself thinking that it would be easier to write arbitrary Clojure code for your new instructions and types, feel free to do so. Just not here.

