# Push Types, Modules and Attributes

Push is _technically_ a strongly-typed language by definition, but to be frank that has little impact on the typical user's understanding of Push programs, literals, and how the `Interpreter` acts given a particular program.

As the `Interpreter` executes a program stored on the `:exec` stack, each item is popped in turn. For each item popped,

- if it is an instruction, that instruction is applied to the `Interpreter` state
- if it is a registered `input` (or other bound variable name), that value is pushed onto the `:exec` stack
- if it is a Clojure `list`, the contents of the list are concatenated onto the top of the `:exec` stack (so the first item in the `list` will be popped next)
- if it is a _recognized Push literal_ it is pushed the the appropriate stack
- if it is unrecognized, then it is pushed to the `:unknown` stack (and ignored)

That second-to-last bullet is the one we need to talk about here.

## Types in Push

### Stacks ≠ Types

Stacks are not types in Push, and the relationship between them can sometimes be confusing. In the core Push language, there are only a few literal types: `:boolean`, `:integer`, `:float`, `:char` and `:string`, and there are stacks associated with those types in every Push `Interpreter` by default. But while both the `:exec` stack and the `:code` stacks exist by default as well, there is no "`:exec` type" or "`:code` type" as such.

Whenever the `Interpreter` is processing an item popped from the `:exec` stack to decide its fate, it will run through a stored list of _recognizer predicates_ to determine where it should route the item. In the case of those basic Push types listed above, I'm sure you can easily visualize the recognizers: the `:char` recognizer is simply the Clojure `char?` function, and so forth.

Note though that there are no `:code` or `:exec` recognizers. These are not _types_, but rather _signifying locations_. Indeed, these stacks are permitted (and expected) to contain compound forms, instructions, inputs, and even unrecognizable items. While the `Interpreter` routing system will never send things to these stacks, numerous _instructions_ exist in the Push language that will put things onto them, whether by moving them explicitly from other locations, or by pushing results of internal calculations.

So you should not be misled into thinking that an item the `Interpreter` would _recognize_ as an `:integer` will always and forever be sent to the `:integer` stack in all circumstances. The `:integer->code` instruction can move an `:integer` to the `:code` stack, or the `:integer-generalize` instruction (not part of the standard library) might send it to the `:number` stack. The thing itself, the _Push form_ implied by the Clojure symbol `781` is still "the integer 781", and its _type_ is still "integer" in Push terms, but it may not reside on the `:integer` stack for its entire life.

Similarly, the items on the "strongly typed" stacks may not be _strictly_ the type you imagine. A Push `:integer` is an arbitrary-precision number, which in Clojure might be represented by an `int`, a `double`, a `bigint` or a `bigdecimal` value. Indeed, as in the host language Clojure, many `:integer` functions automatically "promote" the type of function results to a fitting numeric scale.

In principle, Push types are "strong" in the sense that we treat them by convention that way. But in practice, any instruction—whether intentionally or accidentally—can push any item to any stack, and except for the order of the `Interpreter`'s recognizers, it boils down to the observation of convention where you will see particular items of particular types.

In those rare situations where the result of an instruction you're writing might have an ambiguous type, one should use the `Interpreter` itself to decide where to put the item: send it to `:exec` and let the routing algorithm put it in the right place.

### Defining Push types

Exploring any interesting problem will entail writing new types. In this `Interpreter`, you can think of a "type" as a bundle of associated instruction definitions, _plus a recognizer_. So for example, [the core Push `:integer` type](https://github.com/Vaguery/klapaucius/blob/master/src/push/types/base/integer.clj#L143-L146) is _defined_ as simply as

~~~clojure
(def integer-type
  (push.types.core/make-type :integer
                             :recognized-by integer?
                             :attributes #{:numeric}))
~~~

That is, it has a unique identifier (which is, as it happens, exactly the name of the stack to which it will be sent by the `Interpreter`), it has a `:recognized-by` (which is exactly the function that will send it to that stack from `:exec`), and it has some labeling junk you need not worry about. That's actually the entirety of the type definition.

That said, what I've written above is a type with no associated instructions, so it will probably (but not necessarily) be pretty boring in practice. It will be recognized if it shows up in a program, and it will be sent to the `:integer` stack. I could as easily (and usefully) define a `:robot` type with 

~~~clojure
(def fancy-robot-type
  (push.types.core/make-type  :robot
                              :recognized-by knows-three-laws?
                              :attributes #{:fancy :also-a-robot}))
~~~

and it would "act" in the right way, as far as the `Interpreter` was concerned: if it appeared in a Push program, it would be sent to the `:robot` stack.

And in both cases, these Push literals could later be consumed as arguments by _instructions_ that pop items from particular stacks. In fact, that is the fundamental understanding of what _type_ in Push really is: The type of an item is what the acting instructions _infer_ its type to be, when they consume it as an argument.

Note though that when I defined a Push type a few paragraphs back, I said it was a bundle of "associated instructions". That's where the action really begins.

When you look at the [actual definition of the `:integer` type in the Klapaucius codebase](https://github.com/Vaguery/push-in-clojure/blob/master/src/push/types/base/integer.clj#L143-L146), you'll see it first defines a number of type-specific helper functions, then defines particular `:integer` instructions, and then it makes the tiny little stub with `make-type`, _and then it attaches the instructions to that stub_. Something more like this (as of this writing):

~~~clojure
(def integer-type
  ( ->  (t/make-type  :integer
                      :recognized-by integer?
                      :attributes #{:numeric})
        make-visible 
        make-equatable
        make-comparable
        make-movable
        make-printable
        make-quotable
        make-returnable
        (t/attach-instruction , integer-add)
        (t/attach-instruction , integer-dec)
        (t/attach-instruction , integer-divide)
        (t/attach-instruction , boolean->integer)
        (t/attach-instruction , char->integer)
        (t/attach-instruction , float->integer)
        (t/attach-instruction , string->integer)
        (t/attach-instruction , integer-inc)
        (t/attach-instruction , integer-mod)
        (t/attach-instruction , integer-multiply)
        (t/attach-instruction , integer-sign)
        (t/attach-instruction , boolean->signedint)
        (t/attach-instruction , integer-subtract)
        ))
~~~

I'll deal with the _aspect_ definitions (`make-visible` and `make-quotable` and so forth) in detail in the next section, but let me point out that those are _also_ adding bundles of instructions to the type. And then there's that list of `attach-instruction` commands at the end.

A Push type—at least in this implementation of this `Interpreter`—is in other words _exactly_ a bunch of interrelated instructions, plus a recognizer. That's it, that's all.

So when I speak too glibly about whether we're "using a particular type" or not in a given experiment, all I really mean is this relatively imperative pile of instructions, most of which take arguments from or push results to the `:integer` stack, has been shoved into the `Interpreter` we're running.

In other words, it's _all about the instructions_.

### Modules: `:code` and `:exec`

If you've read through the overview of how Push works, you should still be wondering about `:exec` and `:code`, which are arguably the most important and "expressive" aspects of the language.

As I said, there's no "type" associated with these stacks, and thus there's no recognizer. Items get onto the `:code` and `:exec` stacks by other means (almost always via explicit instruction results being moved or pushed there).

That said, these are crucial collections of instructions, even though there are no literal "items" of this "type", so they are organized into _modules_ (as opposed to _types_). Same idea, same structure, except no recognizer.

[Have a look at the `:code` module definition](https://github.com/Vaguery/push-in-clojure/blob/master/src/push/types/modules/code.clj), and you'll see it's the same structure (more or less) as `:integer` was:

~~~clojure
(def code-module
  ( ->  (t/make-module  :code
                        :attributes #{:complex :base})
        make-equatable
        make-movable
        make-printable
        make-returnable
        make-visible 
        (t/attach-instruction , code-append)
        (t/attach-instruction , code-atom?)
        (t/attach-instruction , code-cons)
        ;; ... blah blah loads more
        (t/attach-instruction , code-wrap)
        ))
~~~

That is, it has a name, `:code`, and some vaguely informative `:attributes`, and then a bunch of instructions. And that's it.

So here's a useful design pattern for you to keep in mind when you're considering a new Push project: _Design in terms of types, but construct in terms of transformations_. If you're going to be using complex numbers, by all means build a complex number Push type, but realize it will (and should) be something like 

~~~clojure
(def my-complex-type
  ( ->  (t/make-type  :complex
                      :recognized-by complex-number?
                      :attributes #{:numeric :complex})
        make-visible 
        make-equatable
        make-movable
        make-printable
        make-quotable
        make-returnable
        (t/attach-instruction , complex-add)
        (t/attach-instruction , complex-complement)
        (t/attach-instruction , complex-divide)
        ...
~~~

In other words, it will end up being the word `:complex`, maybe a simple rule for discriminating it from a regular real number, and then a load of salient complex number _behavior_ in the form of instruction definitions.

### Aspects: Tasty Bundles of Behavior

You'll notice those other functions in the type and module definitions above, the ones that go `make-visible` and `make-equatable`. These are _aspects_, and they create instructions for the type (or module) being defined, which enable behavior shared across many types.

Let's walk through the basic standard ones:

- `make-visible` creates two instructions for a type: `:x-stackdepth` and `:x-empty?`, which permit a certain limited read-only introspection. You'll notice if you peer at the codebase that `:error` is `:visible`, meaning the program can ask how many items are on the `:error` stack, but `:log` is not `:visible`, so that can't be examined.
- `make-equatable` creates two instructions: `:x-equal?` and `:x-notequal?`. These are Push instructions that _consume arguments_, so you definitely don't want them to be available to the `:log` or `:error` modules (and they're not).
- `make-comparable` creates less-than, greater-than, and all the or-equal variations on that theme, plus `:x-min` and `:x-max`. This is for types in which a particular and definite order can be imposed. Technically speaking, the types of things you're comparing _must_ implement Clojure's `compare` function. So if you're going to add that `:complex` type I sketched above, you should be careful to make the Clojure type (or record) you use to implement the `compare` protocol.
- `make-movable` is the "guts" of Push: it creates all those instructions that shuffle things around on stacks: `:x-dup`, `:x-shove` and `:x-flush` and so forth. Again, you'll notice we don't make the `:print` stack `:movable`, because we really don't want it getting shuffled around while the program is running. Or maybe we do; add it if you feel brave.
- `make-quotable` adds a single instruction to a type (or module) which moves things directly to the `:code` stack
- `make-returnable` adds instructions which push items to the `:return` stack [see that document]
- `make-printable` adds instructions which push items onto the `:print` stack

There are more, surely. These are intended to append convenient and simple piles of functionality to the types you define, and also to centralize the behavior of many diverse items in a single location.

Your domains of interest may involve new aspects, and [as you can see in the source code](https://github.com/Vaguery/push-in-clojure/blob/master/src/push/instructions/aspects/comparable.clj) they're _also_ just piles of instructions appended to a type (or module). In this case, the instructions are created dynamically for the type in question, but the result is the same in the end: Your new `:foo` type can be given a `:foo-dup` instruction simply by adding a `make-movable` call to its definition. And you can add a new `:reflecting` aspect to all the other pre-defined types in Push by writing a `make-reflective` function and applying it to the types and modules you use in your project's `Interpreter`.

## Standard Types and the Subtleties of Ontology

The "base" Push types are surprisingly mundane and straightforward. But in the standard library are a number of pre-built "extensions" you can use for your projects. It's worth talking for a minute about the `:vector` and `:vectorized` ones.

In Push, a `:vector` is exactly a Clojure vector: a linear sequence of (arbitrary) items wrapped in square brackets but otherwise only subtly different from a `list`. One nice thing about them, in this context, is that Clojure `list` items will get chopped up automatically by the `Interpreter` when it sees them, so you could for example use `:vector` to represent collections of things, as opposed to "blocks" of things in the sense Push treats `list` items. But… meh.

Let's look instead at the `:vectorized` type(s), which is based on work Tom Helmuth did in Clojush. Unlike most other type definitions in Push, we can use `:vectorized` to automatically construct a complete type _based on another one_. For example, if you invoke the core function `(build-vectorized-type :integer)`, the result will be an entire new Push type, called `:integers`, which is defined as "a `:vector` which contains only `:integer` elements".

Aha! Not so meh, I think.

Notice a few things about these `:vectorized` items. First, they're really a sort of specialized _sub-type_, but they're one we encounter frequently in mathematical modeling and programming: numerical vectors, or character arrays. Second, they're born fully armed with all the "normal things" (instructions) you would do to a linear collection of a particular thing: they come armed with instructions that know how to concatenate two of them, break them apart into components, find things, reverse them, all that stuff.

And of course they're still "also" `:vector` items. So if your domain of interest _asks you_ to do so, you can always implement instructions that generalize them from `:integers` vectors to `:vector` vectors, and then stick other things into them without violating your ontological integrity. Indeed, `:vector` is simply a `:vectorized` sub-type of `:code`, meaning it can "only" contain things of any type whatsoever, or lists of those.

I bring this up because it's easy to misunderstand the relation between the (actually) strict types of the underlying Clojure language in which this interpreter is written, the _nominally_ strong type of Push itself, and these odd little _de facto_ types, like `:integers` or `:strings` vectors. While the internal representations of these data structures are typed, almost everything in a running Push program really depends on what the executed instructions _decide_ to do. The instructions in a generated `:integers` type are internally consistent, and will only put items from an `:integers` vector onto the `:integer` stack... but that's not _checked_.

Keep that in mind as you write libraries of instructions for your own projects, and when you load libraries of instructions, types, modules or aspects others have written.
