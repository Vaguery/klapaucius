# Implementing "higher-order functions" and "macros" as Push `:code`

## Push "function signatures" and macros

Suppose we have some generic Push code like this:

```
'(:8 2.3 :integer-add (:foo-bar false :code-do) 9)
```

If I replace all the ERCs with _getters_, I can see a way for it to be rewritten as a new "instruction" written in the Push DSL with some simple pattern replacement:

~~~clojure
; («integer» «float» :integer-add (:foo-bar «boolean» :code-do) «integer»)

(consume-top-of :integer :as :i-1)
(consume-top-of :float :as :f-1)
(consume-top-of :boolean :as :b-1)
(consume-top-of :integer :as :i-2)
(calculate [:i-1 :f-1 :b-1 :i-2]
    #(list %1 %2 :integer-add ( :foo-bar %3 :code-do) %4) :as :result)
(push-onto :exec :result)
~~~

The resulting "instruction" has simple, easily calculated `:needs` of `{:integer 2 :float 1 :boolean 1 :exec 0}` and `:makes {:exec 1}`.

How can this new function be stored and invoked, though? The good old `:name` stack comes to mind. That is, local _ad hoc_ bindings and references. Or perhaps a `:function` stack?

### trivial macros

Of course the `:code` stack can contain more or less anything (and typically does), so one might find `'(77)` or `'(false)` sitting there. The procedure described above would produce a "macro" like this:

~~~clojure
; («integer»)

(consume-top-of :integer :as :i-1)
(calculate [:i-1] #(list %1) :as :result)
(push-onto :exec :result)
~~~

Not especially exciting, but who are we to judge? That said, this sort of thing could be blocked if it becomes a problem.

## A related path to higher-order functions

TBD