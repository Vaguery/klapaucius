# "Automatically" generated Push instructions

Push types share quite a bit of functionality in the form of common instructions.

For example, almost all (but not all) stacks will have an associated `:foo-empty?` and `:foo-stackdepth` instructions, which return a `:boolean` value `true` if the stack is empty, or an `:integer` count of items respectively. Most types (though again not necessarily all) will also have `:foo-eq?`.

In this implementation, all types (even the simplest ones) are defined as `PushType` records, and this common funcitonality is managed in via the records' attributes.


## Attributes and associated "generic" instruction suites

For a `Pushtype` `:foo` with the given attribute, these `Instructions` will be defined automatically (and stored in the record):

- `permanent`
  - no instruction ever pops arguments from it
- `:visible`
  - `:foo-empty?`
  - `:foo-stackdepth`
- `:comparable`
  - `:foo-eq?` aka `:foo-equal?`
  - `:foo-neq?` aka `:foo-notequal?`
- `:sortable`
  - `:foo-lt?` aka `:foo-lessthan?`
  - `:foo-lte?` aka `:foo-lessthanorequal?`
  - `:foo-gt?` aka `:foo-greaterthan?`
  - `:foo-gte?` aka `:foo-greaterthanorequal?`
  - `:foo-min`
  - `:foo-max`
- `:movable` (combinators)
  - `:foo-dup`
  - `:foo-flush`
  - `:foo-pop`
  - `:foo-rotate`
  - `:foo-shove`
  - `:foo-swap`
  - `:foo-yank`
  - `:foo-yankdup`
  - (and others TBD)
- `:printable`
  - `:foo-print`
- `:quotable`
  - `:foo->code`
  - `:foo->exec`
- `:collectible`
  - implies existence of `:vector-of-foo` and `:set-of-foo` collection types (?)

## Standard Push types

- `:boolean`
  - `:visible, :comparable, :movable, :printable`
- `:char`
  - `:visible, :comparable, :sortable, :movable, :printable`
- `:code`
  - `:visible, :comparable, :movable, :printable, :quotable`
- `:exec`
  - `:visible, :comparable, :movable, :printable, :quotable`
- `:float`
  - `:visible, :comparable, :sortable, :movable, :printable`
- `:integer`
  - `:visible, :comparable, :sortable, :movable, :printable`
- `:string`
  - `:visible, :comparable, :sortable, :movable, :printable`
- `:error`
  - `:visible, :permanent, :printable`

## Getters and Setters

Compound types can be defined with a `:signature`, for example a `:point` might have `{:signature {:x :integer :y :integer}}`. With attribute `:compound` the following functions will be defined:

- `:point-assemble`
- `:point-disassemble`

With the attribute `:settable` the following will also be constructed programatically:

- `:point-set-x`
- `:point-set-y`
- `:point-get-x`
- `:point-get-y`

Caveat: the `:signature` must refer to already-defined types.

## Constrained collections and "pseudotypes"

- `:vector_of_foo` or `:tuple_of_foo`
  - gets all the "standard" vector-manipulation instructions (concatenation, splitting, etc)
- `:set_of_foo`
  - gets all the "standard" set-manipulation instructions (union, container checks)


