---
title: "Klapaucius"
---
# The Klapaucius Push interpreter

This is the core documentation site for the Klapaucius interpreter, written in Clojure.

## The Push language

### Interpreter behavior

### Stacks and instructions

### Basic Push syntax


### Types and modules

#### Numeric

- `:scalar`

  Including integers, floating-point, rational numbers, `BigDecimal` and `BigInteger` values. All in one big pile.
- `:complex`
  
  Numbers in the complex plane, where the `real` and `imaginary` parts are both `:scalar` values.
- `:interval`
  Represent continuous scalar intervals, with fixed `min` and `max` value (both `:scalar` values), either of which may be included as part of the range.

#### Other basics

- `:boolean`
  The values `true` and `false`.
- `:char`
  Pseudo-numeric values representing single characters (from anywhere in the unicode universe).

#### Collections

- `:set`
  Finite unordered collections, containing no more than a single copy of any given item. In general they can contain any items at all, including other `:set` items.
- `:string`
  An ordered collection of `:char` elements. You probably already know what they look like and how they work.
- `:vector`
  An ordered collection of _any_ kind of items. Technically, an ordered collection of zero or more `:code` items.
- `:vectorized` types
  Ordered collection which contain only a single type of element.
  - `:booleans`
  - `:chars`
  - `:complexes`
  - `:scalars`
  - `:intervals`
  - `:refs`
  - `:strings`
- `:tagspace`
  A special `:scalar`-indexed key-value collection, with an approximate lookup function. Items (of any type) are stored at arbitrary _sorted_ `:scalar` indices. When another `:scalar` is used to retrieve an item stored in a `:tagspace`, any item stored in that exact value is returned, or if that specific index is empty, then the item at the next-larger index existing is returned.

#### Programming

- `:code`
  The fundamental Push literal type. All items are `:code`. A `:vector` is a collection of `:code` items.
- `:exec`
  A special stack (and collection of instructions) used by the Push interpreter to execute programs. Technically not a "type" in a strict sense; consider it a collection of `:code` items in a special place.
- `:ref`
  Inputs, arguments, and "local" variables. A `:ref` is always a Clojure keyword, and can be used to refer to a `:binding`.
- `:snapshot`
  A "saved" collection of interpreter stacks, which various Push instructions can save and retrieve.
- `:generator`
  A stateful item which contains a Push item, and responds to various Push instructions by emitting copies of the stored item, cycling through a stored collection one step at a time, and so forth. Similar to a stateful `iterator` in some other languages.
- `:quoted`
  A special wrapper type which can contain any `:code`. When the interpreter encounters a `:quoted` item, it is stored on the `:code` stack instead of being interpreted and acted upon.

#### Non-type stacks

- `:log`
  A special stack to which the Interpreter writes records of each step it takes.
- `:error`
  A special stack to which the Interpreter writes records of runtime errors: missing arguments, division by zero errors, violations of size constraints, and so forth.
- `:return`
  A special stack to which the Interpreter can push any `:code` values.
- `:print`
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
