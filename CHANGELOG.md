# Recent changes

## current work

- Refactored `:interval-subtract` to produce a continuation form which adds the negative of the second argument (the same way `:interval-divide` had already produced a continuation form which multiplied by the reciprocal(s)).
- Pushed basic interval arithmetic functions up into the type definition, rather than having them in `calculate` blocks in the instructions themselves.
- Filled in `future-facts` in `:interval` type. Specifically, `:interval-multiply` and `:interval-add` were not actually doing the math right for empty intervals. Now there is an explicit check in each which treats an empty `:interval` item as identity in addition, and zero in multiplication. That is, when only one addend is empty, the other is returned unchanged; when both addends are empty, `nil` is returned; when _either_ multiplicand is empty, `nil` is the result.
- Refactored interpreter DSL. In earlier versions, PushDSL instructions operated on an ad hoc sort of tuple, containing the interpreter and a "scratch" space for intermediate variable-shuffling. The `Interpreter` record now has an explicit `:scratch` field, which is used for the same purpose. This eliminates all the tuple-shuffling and makes the DSL code much more readable.
- Added `:x-sort-instruction` to the `:vectorized` types. This is _only_ added to vector types where the underlying item type has the `:comparable` attribute.
- Added `push.type.core/conditional-attach-instruction`, which only attaches a given instruction to a type if a predicate value is `true`, and otherwise returns the type unchanged.
- modified the way all index lookups are done with non-integer scalar values. Now they round "up and around" when picking the index of a collection. The value is first reduced using `(mod x (count coll))`, as before. However, now its `ceiling` is taken, where before its `floor` was used. Thus, item `0.1` of a collection is the _second_, item `1.1` the third, and so on. The "wrapping" comes in when we exceed the count of the collection: For a collection of five items, item `4` is the last one (as normal), but item `4.1` is the first one. Item `-0.1` is also the first one; both `-1` and also `-1.1` refer to the last item.
- modified `:tagspace` lookup process. It now "wraps around" (see next item). First, the range of the (max-min) tagspace keys is calculated (returns 0 if either is infinite, or there is only one). Then the _average_ size of gaps between keys is calculated. This average is added to the range, and this value is used to reduce the lookup value. For example, if the keys are `[1 2 7 13]`, the range is `12` and the average gap is `12/3=4`. Lookup values are reduced `mod 16`. This "modded index" is then used for lookup as was the case:
  - `(1,2] => key 2`
  - `(2,7] => key 7`
  - `(7,13] => key 13`
  - `(13,16] => key 1`
- modified `push.interpreter.core/handle-item` to write the current item being processed to the `:current-item` entry of the `Interpreter` record (technically not part of the record, just an annotation). This enables _much_ more informative error reporting, and potentially more detailed and straightforward tracing of program execution.
- added size limits for `push.instructions.dsl/bind-item` based on stack size limits: if the sum of the count-collection-points(item) and count(binding stack) is too large, it balks and adds an `:error` item to that stack
- fixed a bug in `push.instructions.dsl/bind-item` that saved the scratch ref, not the value stored in it
- added size-checking (using `:max-collection-size` as a limit) to bind-value
- fixed a concern (maybe a bug?) in `push.interpreter.core/bind-value` by type-casting binding "stacks" to lists
- moved `push.instructions.dsl/insert-as-nth` to `push.util.code-wrangling/insert-as-nth`
- moved `push.instructions.dsl/delete-nth` to `push.util.code-wrangling/delete-nth`
- moved `push.instructions.dsl/list!` to `push.util.code-wrangling/list!`
- fixed several bugs involving infinite and `NaN` `:scalar` value arguments in various functions
- refactoring and rewriting "stress test" functions to work with Claypoole threading


## 0.1.23

- added `buildable` aspect
- added `:parts` and `:builder` keys to PushType `record` and `make-type` function; `:parts` is a manifest of the stacks from which components are to be taken; `:builder` is the function used to compose them into an item of this type
- added `:x-make` instruction in the `buildable` aspect, which pops the requisite parts (in the correct order) and makes a new `:x` item
- added `:x-parts` instruction in the `buildable` aspect, which pops an `:x` and decomposes it into a code block of its component parts (in the correct order), which is pushed to `:exec`
- fixed a bug in `make-tagspace` which threw an exception when storing a non-terminating rational and a `bigdec` in the same `TagSpace` (added a `dire` handler that applies `(with-precision 1000 ...)`)
- moved `:push-quoterefs`, `:push-unquoterefs`, `:push-discardARGS` and `:push-storeARGS` instructions to new `push.type.module.behavior`
- modified `push.interpreter.core/apply-instruction` to recognize and respond to `:cycle-args?` state in `:config`
- refactored `push.interpreter.core/apply-instruction` for clarity and extensibility
- tested `push.interpreter.core/apply-instruction`
- added DSL instructions `start-cycling-arguments` and `stop-cycling-arguments`, and added tests for those plus older `start-storing-arguments`, `stop-storing-arguments`
- renamed `push-discardARGS` to `:push-nostoreARGS`
- added instructions `:push-cycleARGS` and `:push-nocycleARGS` to `push.type.module.behavior`
- modified `:scalar-power` overflow heuristic: now it (1) counts the characters in a rational base, and (2) cuts off when `(* exponent (log base))` is larger than 32768.
- fixed a time-out problem caused by `:x-fillvector` (and related instructions) building huge vectors from huge components; added a size limit, and also reduced the scaling factors to max 1000. Also in `:ref-fillvector` and `:ref-cyclevector` (though no oversize check for that yet)

## 0.1.22

- Added `:interval` type
- Cleaned up handling of ∞/-∞ throughout
- Added `:complex-infinite?` and `:scalar-infinite?` instructions
- Fixed bug in `:interval-scale` results' bounds, when scaling factor is negative
- added `:scalars-filter` and `:scalars-remove` instructions
- removed `with-precision` wrapper on `push.type.definitions.tagspace/make-tagspace`
- added `:scalars-split` instruction
- renamed `:tagspace-split` to `:tagspace-cutoff`
- added `:tagspace-filter`, `:tagspace-remove` and `:tagspace-split` using `:intervals` for filtering
- added `:X-tagstack` instruction to `taggable` aspect
- extensive cleanup of DSL exception-handling (nonexisting stacks created on demand now)
- added `:collectible` aspect, which adds several `:set`-related instructions to types
- added `:x-vfilter`, `:x-vremove` and `:x-vsplit` instructions to `vectorized` aspect, which filter items of a vectorized type with another item of the same type
- added several `:tagspace` instructions for emitting keys and values as `:set` or `:vector` items
- added several `:tagspace` instructions for filter/remove/splitting with a `:set` acting on the _values_ (not the keys); a sort of reverse-lookup
- added `:x-pt-crossover` and `:x-distinct` functions to `:vectorized`
- `:x-items` (in `:vectorized`) recycles items onto :exec
- `:x-fillvector` (in `:vectorized`) constructs vector of N copies of top root item, using a second scalar to determine relative size of N (few, some, many, lots)
- `:x-cyclevector` (in `:vectorized`) constructs vector of N items by cycling through root stack, using a second scalar to determine relative size of N (few, some, many, lots)
- `:ref->vector` copy :ref stack to a :vector
- `:ref-fillvector` constructs vector of N copies of top :ref item, using a second scalar to determine relative size of N (few, some, many, lots)
- `:ref-cyclevector` constructs vector of N items by cycling through :ref stack, using a second scalar to determine relative size of N (few, some, many, lots)
- added new `:vectorized` instructions to `:vector` as appropriate
- changed `:x-print` instruction to return the string result of `pr-str`
- modified PushDSL `:needs` and `:products` table to remove 0 values; since it was originally written, stacks are created on demand
- cleaned up `push.type.item.vector` and `push.type.item.vectorized` call structures for legibility
- added `forget-instructions` and `register-instructions` functions to `push.core`
- `ref-dump-tagspace` instruction
- renamed `collectible` aspect to `set-able` throughout
- imposed a restriction on `:scalar-power`: produces an `:error` when `(abs (*' base (Math/log expt)))` is larger than 65535 (that magic number could change after stress testing), to avoid time-out errors in random programs generating huuuuuuuuuge results blithely
- minor change to `:scalar-power`, which will produce an `:error` if the result is `∞` or `-∞` (unlike most other instructions)
- fixed problematic issue (not a bug, but causative of one) converting `:scalar` values to indices of collections; removed all unnecessary references to `long` type in codebase (used only for converting `char` values to numbers now)
- removed unnecessary references to `bigint` and `bigdec` as well
- fixed several subtle reversions (bugs) caused by change to non-integer indexing


## 0.1.21

- Some minor twiddling of the way instructions are executed (in `push.instructions.dsl/consume-top-of` and `push.instructions.dsl/consume-nth-of`, plus `push.interpreter.core/apply-instruction`) means that the entire tuple of the updated interpreter and the `scratch` map built during execution of an instruction are both returned to the interpreter. This permits:
- Added a setting in the interpreter's `:config` hash called `:store-args?`. When this is `true` (it's `false` by default), the arguments consumed by an instruction are pushed as a code block onto the special `binding` called `:ARGS`. All interpreters have this `binding` (though it doesn't do anything special in most cases); it's just a nominal hook for:
- Added several instructions to the `:ref` type.
  - `:ref-ARGS` pushes the current value of `:ARGS` to `:exec` and also the keyword `:ARGS` onto `:ref`
  - `:push-storeARGS` sets the interpreter's `:store-args?` value
  - `:push-discardARGS` unsets the interpreter's `:store-args?` value
  - `:ref-peek`, which looks up a `:ref` and pushes its current value to `:exec` but also replaces the `:ref` on that stack
- also various bug fixes, noticed along the way


## 0.1.20

- `push.instructions.dsl/calculate` (the core of the Push DSL, really) now uses [`dire` error-handling](https://github.com/MichaelDrogalis/dire) to short-circuit exceptions raised by common instruction operations. This includes Division by zero, Non-terminating decimal representations, and the odd "Infinity or NaN" error. The DSL step now automatically writes an `:error` to that stack and returns a result (of `calculate`) of `nil`. It's up to the instruction definition to handle that result.
- have not yet added `NaN` errors to this system, but it will be considered for 0.1.21
- added numerous tests of edge-conditions in functions using `:scalar` arguments, especially when a `bigdec` and a `rational` are both arguments. The `bigdec` tends to try to convert the `rational` to an arbitrary-length `bigdec`, and if it's a non-terminating decimal (like `1/3`) it will raise an exception


## 0.1.19, 0.1.18, 0.1.17

- `:scalar` type has replaced all references to both the `:integer` and `:float` type
- indexing with arbitrary scalars is done by first reducing the "index" `modulo` the size of the collection, and then "sliding" upwards to the next-largest integer; if the index is still larger than the size of the collection after `modulo` reduction, the first item is returned
- `:string->integer` and `:string->float` have been removed until a more effective and robust recognizer algorithm is available
- numerous instructions have been combined due to the collapse of `:integer` and `:float` to a single type
- the routing function of the interpreter has been rewritten from scratch to permit
  - more flexible `:recognizer` predicates
  - arbitrary preprocessing items before they are sent to their destination stacks
  - differences between the `:name` of a type and its `:target-stack`
- the interpreter now recognizes `QuotedCode` items, which (using the features just described) are routed to `:code` after being "unwrapped". Convenience function `push-code` creates a `QuotedCode` record from an arbitrary Push item or code block; when it is routed by the interpreter it is sent to `:code`, but the `QuotedCode` "wrapper" s removed. This permits more flexibility in continuation results from instructions.
- `:scalar-power` has been disabled until an overflow (which leads to timeouts) can be resolved
- several arithmetic instructions used to return their arguments to their sources when they produced an `:error` (such as square root of negative numbers, or arcsin of a number out of range). These now consume their arguments.


## 0.1.16, 0.1.15, 0.1.14

- interpreter routing now uses `PushRouter` records, which are stored in each type definition
- when invoking `make-type`, an optional `:router` keyword argument can be assigned a `PushRouter` instance, which in turn can define a `:preprocessor` (which can transform the item before it is pushed), and a `:target-stack`, which names its destination stack; default `:preprocessor` is `identity`, and default `:target-stack` is the type name
- for backwards-compatibility, `make-type` still accepts a `:recognizer` argument; if a `:recognizer` is given (but no `:router`) then a `router` with the default values is constructed implicitly
- various introspection instructions, mainly focused on `:ref` and `:binding` manipulation


## 0.1.13

- changed behavior of `:x-yank`, `:x-yankdup` and `:x-shove` instructions to work like Clojush's; that is, rather than using modulo indices, it uses constrained ranges (negative index -> 0, overlarge index -> max item)

## 0.1.12, 0.1.11

- various bugfixes and refactoring

## 0.1.10

- added `:tagspace` type
- added `:generator` type
- restructured `push.types` tree
- various small changes to instruction sets
- removed `plane_geometry` references from main codebase
- cleaned up most type and module definition files

- rewrite `require` blocks for ClojureScript compatibility

## 0.1.9

- `:x-againlater` and `:x-later` instructions added to `:movable` aspect
- `:ref-lookup`, `:ref-dump`, `:ref-fullquote`, `:push-quoterefs` and `:push-unquoterefs` instructions for working with `:ref` bindings
- several cleanups of midje tests
- several DSL instructions added in support of new instructions

## 0.1.8

- debugging bump; some obsolete documentation updated

## 0.1.7

- `inputs` renamed `bindings` throughout
- each `binding` is now a _stack_ rather than a simple `var` now; assignment pushes new values onto the stack without deleting earlier values

## 0.1.6

- various numeric functions

## 0.1.5

- `core.clj` exposes basic `interpreter` creation functions for use as a library
