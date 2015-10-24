# Push Instruction DSL

The Push Instruction DSL is a highly-constrained domain-specific language for defining Push instructions.

An Instruction operates on an Interpreter by sending it (thread-first) through the steps of the DSL script, producing a changed Interpreter at each step, and (optionally) recording and manipulating ephemeral scratch variables as it does so. These scratch variables are always local to the DSL interpreter environment, and are discarded as soon as the Instruction code ends.

## DSL fundamentals

For several practical reasons the Push Instruction DSL is simple but expressive, and will feel highly constrained compared to writing "freehand" Clojure code. While the definitions of some instructions may require more characters than you might normally type in an _ad hoc_ project, by using the DSL the `Interpreter` is able to:

- automatically infer the stack types and minimum number of items that must appear on those stacks
- simplify and optimize coded definitions _in the background_
- automatically generate unit tests for edge cases (_e.g._, missing arguments)
- monitor and summarize interactions and similarities between registered Instructions _from their source code_, simplifying the design, implementation, and management of new types, instructions and experiments
- better support validation of complex Instruction types
- change behavior in response to "missing instructions" or "redefined instructions" automatically
- change Interpreter behavior globally without changing the code of each Instruction (for example, one can easily explore the behavior of programs when arguments are _not deleted_ by calling Instructions)
- maintainability of the Push interpreter codebase
- simplified logging and debugging information through globally-controlled side-effects

As I said, there are some constraints:

- only one item can be popped from a stack in any given step
- if an item is to be referred to in a later step, it _must_ be stored in a named scratch variable
- arbitrary Clojure functions can be invoked, but it can _only_ use named scratch variables or inline literals as arguments
- the general state of the `Interpreter` is not available; only stack items and a few other state variables can be read; only stacks can be written

## DSL scratch variables

All scratch variables are referred to by Clojure keywords (not symbols). These are keys of a transient local store, and you shouldn't have to worry about namespace leakage. However they should be unique; saving a new value to an already-defined keyword key will overwrite the old value.

## DSL transactions

When a Push `Instruction` is invoked, it receives the `Interpreter` state as its (only) argument. You can visualize this as though the `Interpreter` is passed through each step of the defined _transaction_, and is transformed along the way.

So for example, the `integer_add` instruction can be defined by this _transaction_:

```clojure
(
  (consume-top-of :integer :as :int1)
  (consume-top-of :integer :as :int2)
  (calculate [:int1 :int2] #(+ %1 %2) :as :sum)
  (put-onto :integer :sum)
)
```

When this transaction is turned into a _function_, the actual Clojure is something more like this:

```clojure
(fn [interpreter]
  ( -> [interpreter {}]
       (consume-top-of , :integer :as :int1)
       (consume-top-of , :integer :as :int2)
       (calculate , [:int1 :int2] #(+ %1 %2) :as :sum)
       (put-onto , :integer :sum)
       (first , )))
```

I've used the comma (whitespace in Clojure) to indicate the places in each line where the threaded argument is invoked. In other words, what's threaded through the instructions (using Clojure's `thread-first` macro, `->`) is a vector composed of the interpreter and the `scratch` hashmap, where local state is stored in the process of running the code.

Note that none of the `scratch` information exists at the start of the transaction, and it is all deleted at the end. Only the `Interpreter` state can be changed.

## DSL instructions

- [x] `count-of [stackname :as local]`

  stores the number of items in stack `stackname` in scratch variable `local` (a keyword)

- [ ] `consume-top-of`
  - [X] `consume-top-of [stackname]`
    
    pop an item (and discard it) from `stackname`
  - [X] `consume-top-of [stackname :as local]`
    
    pop an item from `stackname` and store under key `local`; raise an Exception if `stackname` is empty or undefined

- [ ] `consume-nth-of`
  - [ ] `consume-nth-of [stackname :at where :as local]`
    
    remove the item in position `where` from the named stack, and store it in scratch variable `local`; if `where` is an integer literal, the index of the item removed is `(mod where (count stackname))`; if `where` is a keyword, the value is obtained from the scratch storage; if the scratch value is not an integer (including `nil`), an Exception is raised 
    
  - [ ] `consume-nth-of [stackname :at where]`
    
    remove the item in position `where` from the named stack (and don't store it); if `where` is an integer literal, the index of the item removed is `(mod where (count stackname))`; if `where` is a keyword, the value is obtained from the scratch storage; if the scratch value is not an integer (including `nil`), an Exception is raised

  - [ ] `consume-nth-of [stackname :as local]`
    
    remove the item at the top of the named stack, and store it in scratch variable `local`

  - [ ] `consume-nth-of [stackname]`
  
    remove the item at the top of the named stack and throw it away

- [ ] `consume-stack`
  - [ ] `consume-stack [stackname :as local]`
  
    save the entire named stack into `local` and clear it in the `Interpreter`

  - [ ] `consume-stack [stackname]`

    empty the named stack in the `Interpreter`


- [ ] `calculate [args fn :as local]`
  
  `args` is a vector of keywords, referring to scratch variables

  `fn` is an arbitrary Clojure inline function which must refer to the arguments positionally (as in `(calculate [:int1 :int2] #(+ %1 %2) :as :sum)`); the function can refer to Clojure symbols defined outside the instruction, and can contain literals, but cannot refer have _arguments_ other than those provided

  The result of invoking `fn` on `args` is saved into scratch variable `local`

- [ ] `remember-top-of [stackname :as local]`

  store the top item from `stackname` under key `local`

- [ ] `remember-nth-of`
  - [ ] `remember-nth-of [stackname :at where :as local]`
    
    store item in `stackname` at position `where` under key `local`
  - [ ] `remember-nth-of [stackname :as local]`
    
    store top item in `stackname` under key `local` (same as `remember-top-of`)

- [ ] `remember-stack [stackname :as local]`

  save the entire stack to scratch variable `local`

- [ ] `put-onto [stackname local]`

  push the scratch value `local` onto the top of the named stack

- [ ] `replace-stack [stackname local]`
  
  replace the indicated stack with the scratch variable `local`; if its value is a list, replace the entire stack with that list; if the value is not a list, then replace the stack with a new one containing only that value

- [ ] `instructions [:as local]`

  save a list of all registered instructions in `local`

- [ ] `inputs [:as local]`

  save a list of all registered inputs in `local`

- [ ] `counter [:as local]`
  
  save the current interpreter counter value in `local`


### Possible future extensions

- `(consume-all-of % :integer :foo :boolean :bar :integer :baz :float :qux)`
- `(place-all-of % :integer :foo :boolean :bar :integer :baz :float :qux)`
- ?