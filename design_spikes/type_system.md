# Notes on the type system


### Klapaucius types (as of this writing)

- `:boolean`
  - `:code`
- `:booleans`
  - `:code`
  - `:collection`
  - `:vector`
- `:char`
  - `:code`
- `:code` (everything is `:code`)
- `:float`
  - `:code`
  - `:number`
  - `:scalar`
  - `:complex`
  - `:ratio`
- `:floats`
  - `:code`
  - `:collection`
  - `:vector`
- `:generator`
  - `:code`
  - `:collection`
  - `:structured` (`:state` `:code`, `:step-function` ???, `:origin` `:code`)
- `:integer`
  - `:code`
  - `:number`
  - `:float`
  - `:scalar`
  - `:complex`
  - `:ratio`
- `:integers`
  - `:code`
  - `:collection`
  - `:vector`
- `:instruction`
  - `:code`
  - `:keyword`
- `:ref`
  - `:code`
  - `:keyword`
- `:set`
  - `:code`
  - `:collection`
- `:string`
  - `:code`
  - `:collection`
  - `:vector`
- `:strings`
  - `:code`
  - `:collection`
  - `:vector`
- `:tagspace`
  - `:code`
  - `:collection`
  - `:structured` (`:keys` `:scalar`, `:values` `:code`)
- `:vector`
  - `:code`
  - `:collection`


### Attribute matrix

| type | comparable | cycling | equatable | into-tagspaces | movable | printable | quotable | repeatable | returnable | storable | taggable | visible |
|---|---|---|---|---|---|---|---|---|---|---|---|---|
| `:boolean` |
| `:booleans` |
| `:char` |
| `:code` |
| `:float` |
| `:floats` |
| `:generator` |
| `:integer` |
| `:integers` |
| `:instruction` |
| `:ref` |
| `:set` |
| `:string` |
| `:strings` |
| `:tagspace` |
| `:vector` |
| `:codeblock` |
| `:collection` |
| `:keyword` |
| `:number` |
| `:scalar` |
| `:structured` |

### Abstract types (not explicit in Klapaucius yet)

- `:codeblock`
  - `:code`
  - `:collection`
- `:collection`
  - `:code`
- `:keyword`
  - `:code`
- `:number`
  - `:code` 
- `:scalar`
  - `:code`
  - `:number` 
- `:structured`
  - `:code`
  - `:collection`

### Planned types

- `:matrix`
  - `:code`
  - `:collection`
  - `:vector`
  - `:structured` (`:rows` `:numbers`, `:columns` `:numbers`, `:dimensions` `:positives`)
- `:complex`
  - `:code`
  - `:number`
  - `:collection`
  - `:vector`
  - `:structured` (`:imaginary` `:scalar`, `:real` `:scalar`)
- `:rational`
  - `:code`
  - `:number`
  - `:collection`
  - `:vector`
  - `:scalar`
  - `:structured` (`:whole` `:integer`, `:numerator` `:integer`, `:denominator` `:integer`)
- `:function`
  - `:code`
  - `:codeblock`
  - `:structured` (`:arguments` ??? `:returns` ???)

### Some example domin-specific types

- `:point`
  - `:code`
  - `:collection`
  - `:vector`
  - `:structured` (`:dimension` `:positive`, `:coordinates` `:numbers`)
- `:algebraic`
  - `:code`
  - `:number`
  - `:complex`
  - `:scalar`
  - `:structured` (`:real` `:scalar`, `:imaginary` `:scalar`, `:closed-form` ???)
- `:circle`
  - `:code`
  - `:collection`
  - `:structured` (`:origin` `:point`, `:radius` `:positive`)
- `:positive`
  - `:code`
  - `:number`
  - `:scalar`
  - _may be_ any of the other `:number` types, depending on the parser
- `:one-d-cellular-automaton`
  - `:code`
  - `:collection`
  - `:structured` (`:inputs` `:integers`, `:transition-rule` `:integers`, `:states` `:positive`)

### Some definitions for abstract types

- `:structured`
  
  a `hashmap` with specified keys; anything complex enough to be represented as a Clojure `record`
- `:collection`
  
  any `list`, `codeblock` `map`, `tagspace` or equivalent 
- `:number`
  
  You can probably figure this out.
- `:scalar`
  
  Any one-dimensional numeric value; all scalars can be sorted.
