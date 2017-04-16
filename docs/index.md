---
title: "Klapaucius"
---
# The Klapaucius Push interpreter

This is the core documentation site for the Klapaucius interpreter, written in Clojure.

## The Push language

Push is a stack-based language invented by [Lee Spector](http://faculty.hampshire.edu/lspector/push.html), [Maarten Keijzer](http://www.cs.bham.ac.uk/~wbl/biblio/gp-html/MaartenKeijzer.html)  and their colleagues. It's a very peculiar language, with a lot of features that will seem unfamiliar and even off-putting to modern programmers, because it's _not intended to be used by human programmers_.

Rather, Push (and the many variations that have cropped up through the years) is intended as a representation framework for _automatically generated_ code, especially (but not exclusively) that the sort produced by genetic programming.

Nonetheless, Push is a simple, extensible programming paradigm. It's easily extended for domain-specific problems, and beyond a few deep quirks that make it more robust under the sort of syntax-destroying transformations that happen in the course of automated code generation, it tends to be much more readable than many counterparts you'll see in tree-based, Cartesian, grammatical and other genetic programming paradigms.

### The Klapaucius Push dialect

In building this particular Push interpreter for my own genetic programming work, I've designed it to be more readily extended than most. As a result, there are features of both the underlying Clojure codebase and the way the library has been and is intended to be extended that will affect the user experience. I'll go into much more detail in the individual documentation below, but the core features that set Klapaucius apart from other Push interpreter implementations are:

1. The `PushDSL` for writing instructions: Push is fundamentally a collection of imperative _instructions_, executed serially, which affect the global state of a large collection of type-based stacks. In Klapaucius, these instructions are written in a specially-designed domain-specific language, using a small vocabulary of about two dozen Clojure functions. These DSL functions, or "steps", make the composition of new instructions easier and also more formal, but they have the added benefit of permitting the `Interpreter` itself to collapse arbitrary collections of Push instructions and literals into _higher-order functions_. As a result, evolved Push code build in Klapaucius has the ability to discover abstracted and reusable code more easily than its peers.
2. Robust indexing: One of the fundamental programming tasks that has traditionally been very difficult for genetic programming to work with is the use of _collections_ and iteration. In the Klapaucius Push dialect, there are lots of collections: arbitrary `:code` blocks, `:vector` ordered collections of various types, unordered `:set` collections, ordered `:strings` composed of characters, the indexed key-value `:tagspace` collection type, `:binding` stacks, and the central stacks on which code is manipulated during execution. Code that wants to read or manipulate individual components of these collections is often dependent on fiddly `integer`-based ordinal indexing, and can become a weak spot for automated code generation. Here, we can use _arbitrary scalar values_ to index the contents of collections. Not just positive integers but any floating-point, rational, and even negative or infinite values can be used to reference individual items in finite-sized collections, using a consistent _approximate modulo indexing_ approach that all collections share.
3. Flexible data structure composition and templating: TBD
4. Introspection: TBD
5. Size limits: TBD
5. Surfaced error-handling: There's a long tradition in genetic programming to use "runtime safe" variants of otherwise "risky" operations, like "protected division" that will produce a "safe" `0` result when some random code being executed attempts to divide by zero. Here, I've taken a more _instructive_ approach with these low-level risky functions. Instead of emitting a "fake" result and barreling on, Klapaucius programs that attempt to divide by zero or take the `arcsin(88)` will produce an `:error` result instead of the "expected" numeric result. As `:error` messages accumulate in the read-only `:error` stack, evolved code has the opportunity to interrogate that stack, and potentially to "learn" from earlier missteps.
6. The Halting Problem: TBD

### Interpreter behavior

### Stacks and instructions

### Basic Push syntax

### Types and modules

#### Numeric

- `:scalar` \\
  Including integers, floating-point, rational numbers, `BigDecimal` and `BigInteger` values. All in one big pile.
- `:complex` \\
  Numbers in the complex plane, where the `real` and `imaginary` parts are both `:scalar` values.
- `:interval` \\
  Represent continuous scalar intervals, with fixed `min` and `max` value (both `:scalar` values), either of which may be included as part of the range.

#### Other basics

- `:boolean` \\
  The values `true` and `false`.
- `:char` \\
  Pseudo-numeric values representing single characters (from anywhere in the unicode universe).

#### Collections

- `:set` \\
  Finite unordered collections, containing no more than a single copy of any given item. In general they can contain any items at all, including other `:set` items.
- `:string` \\
  An ordered collection of `:char` elements. You probably already know what they look like and how they work.
- `:vector` \\
  An ordered collection of _any_ kind of items. Technically, an ordered collection of zero or more `:code` items.
- `:vectorized` types \\
  Ordered collection which contain only a single type of element.
  - `:booleans`
  - `:chars`
  - `:complexes`
  - `:scalars`
  - `:intervals`
  - `:refs`
  - `:strings`
- `:tagspace` \\
  A special `:scalar`-indexed key-value collection, with an approximate lookup function. Items (of any type) are stored at arbitrary _sorted_ `:scalar` indices. When another `:scalar` is used to retrieve an item stored in a `:tagspace`, any item stored in that exact value is returned, or if that specific index is empty, then the item at the next-larger index existing is returned.

#### Programming

- `:code` \\
  The fundamental Push literal type. All items are `:code`. A `:vector` is a collection of `:code` items.
- `:exec` \\
  A special stack (and collection of instructions) used by the Push interpreter to execute programs. Technically not a "type" in a strict sense; consider it a collection of `:code` items in a special place.
- `:ref` \\
  Inputs, arguments, and "local" variables. A `:ref` is always a Clojure keyword, and can be used to refer to a `:binding`.
- `:snapshot` \\
  A "saved" collection of interpreter stacks, which various Push instructions can save and retrieve.
- `:generator` \\
  A stateful item which contains a Push item, and responds to various Push instructions by emitting copies of the stored item, cycling through a stored collection one step at a time, and so forth. Similar to a stateful `iterator` in some other languages.
- `:quoted` \\
  A special wrapper type which can contain any `:code`. When the interpreter encounters a `:quoted` item, it is stored on the `:code` stack instead of being interpreted and acted upon.

#### Non-type stacks

- `:log` \\
  A special stack to which the Interpreter writes records of each step it takes.
- `:error` \\
  A special stack to which the Interpreter writes records of runtime errors: missing arguments, division by zero errors, violations of size constraints, and so forth.
- `:return` \\
  A special stack to which the Interpreter can push any `:code` values.
- `:print` \\
  A special stack to which the Interpreter can push the _printed_ string version of any `:code` values.

### Interpreter states


### Arguments and return values

### Special instructions

### Errors

### Program termination

## Extending the language

### Type, module or both?

### Designing new instructions

### The Push DSL

### Stress-testing your libraries
