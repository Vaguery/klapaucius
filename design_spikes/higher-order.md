# Implementing first-order and "higher-order functions" as Push `:code`

## Push "function signatures" and first-order functions

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

### trivial functions

Of course the `:code` stack can contain more or less anything (and typically does), so one might find `'(77)` or `'(false)` sitting there. The procedure described above would produce a "function" like this:

~~~clojure
; («integer»)

(consume-top-of :integer :as :i-1)
(calculate [:i-1] #(list %1) :as :result)
(push-onto :exec :result)
~~~

or even

~~~clojure
; (:foo-bar)

(calculate [] #(:foo-bar) :as :result)
(push-onto :exec :result)
~~~


Not especially exciting, but who are we to judge? That said, this sort of thing could be blocked if it becomes a problem.

## A path to higher-order functions

Suppose we have defined and saved our example above, and `'(:8 2.3 :integer-add (:foo-bar false :code-do) 9)` has become an invocable `:function-1`.

What all can we do that isn't just calling it and putting it onto the :exec stack?

Well, for one thing we can map one or more of its arguments over collections. That is, where `:function-1` wants an `:integer` input, we could map over an `:[integer]` input. Suppose we think of the implicit "call" over its four arguments positionally, as `(:function-1 [:integer :float :boolean :integer])`. Now imagine a `:fn-map-arg` which takes a `:function` and an `:integer`, does Push-flavored bounds-checking on the `:integer` so it points to the range `[0,3]`, and infers that we're mapping that over a `vector-of-x` item.

Say we've picked `:integer 7`; this maps to position 3, so we're mapping over `:[integer]`. We  pop the top `:[integer]` and get `[8 2 -3]`.

Already getting complicated, so let me draw it out. Suppose we encounter `:fn-map-arg` in this state:

```
:fn       | :function-1
:boolean  | T F T F T F
:float    | 1.1 2.2 3.3 4.4
:integer  | 7 1 2 3 4 5 6 7 8 9
:[integer]| [8 2 -3]
:exec     | :fn-map-arg
```

This can be read as "map the top `:function` over a collection item of a type suitable for its `7`th index". The `7`th index is actually its second `:integer` (because Push is intrinsically kind to index values), and so we are mapping `:function-1` over its (internally defined) `:i-1` argument, as elements of the top `:[integer]`.

First let me consume the instruction and arguments of `:fn-map-arg` (`:function-1`, `7`, and `[8 2 -3]`).

```
:fn       | 
:boolean  | T F T F T F
:float    | 1.1 2.2 3.3 4.4
:integer  | 1 2 3 4 5 6 7 8 9
:[integer]| 
:exec     | 
```

The _result_ of `:fn-map-arg` is then (remembering that `:function-1` is `#(list %1 %2 :integer-add ( :foo-bar %3 :code-do) %4)`

```
:fn       | :function-1
:boolean  | F T F T F
:float    | 2.2 3.3 4.4
:integer  | 2 3 4 5 6 7 8 9
:[integer]| 
:exec     | (8 T 1.1 1 :function-1) (7 [2 -3] :fn-map-arg)
```

Let's move ahead:

```
:fn       | :function-1
:boolean  | T F T F T F
:float    | 1.1 2.2 3.3 4.4
:integer  | 8 1 2 3 4 5 6 7 8 9
:[integer]| 
:exec     | :function-1 (7 [2 -3] :fn-map-arg)
```

Now we evaluate `:function-1`:

```
:fn       | :function-1
:boolean  | F T F T F
:float    | 2.2 3.3 4.4
:integer  | 2 3 4 5 6 7 8 9
:[integer]| 
:exec     | (1 1.1 :integer-add (:foo-bar T :code-do) 8) (7 [2 -3] :fn-map-arg)
```

... and keep moving ahead until we reach the next `:fn-map-arg`

```
:fn       | :function-1
:boolean  | T F T F T F
:float    | 1.1 2.2 3.3 4.4
:integer  | 7 8 4 3 4 5 6 7 8 9
:[integer]| [2 -3]
:exec     | :fn-map-arg
```

which produces:

```
:fn       | :function-1
:boolean  | F T F T F
:float    | 2.2 3.3 4.4
:integer  | 4 3 4 5 6 7 8 9
:[integer]| 
:exec     | (2 T 1.1 8 :function-1) (7 [-3] :fn-map-arg)
```

and eventually

```
:fn       | :function-1
:boolean  | T F T F T F
:float    | 1.1 2.2 3.3 4.4
:integer  | 8 2 4 3 4 5 6 7 8 9
:[integer]| 
:exec     | :function-1 (7 [-3] :fn-map-arg)
```

and then

```
:fn       | :function-1
:boolean  | F T F T F
:float    | 2.2 3.3 4.4
:integer  | 4 3 4 5 6 7 8 9
:[integer]| 
:exec     | (2 1.1 :integer-add (:foo-bar T :code-do) 8) (7 [-3] :fn-map-arg)
```

and so on. I might have lost the thread.

That is, like other continuations in Push, the vector of integers has been popped and reduced, and will be called again after `:function-1` has been handed its first element. Assuming everything goes as planned, the last call of `:fn-map-arg` will not have its `:needs` met, and something else will happen. Or not.