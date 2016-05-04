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
- `:code`
  - `:code`
- `:codeblock`
  - `:code`
  - `:collection`
- `:float`
  - `:code`
  - `:number`
  - `:complex`
  - `:ratio`
- `:floats`
  - `:code`
  - `:collection`
  - `:vector`
- `:generator`
  - `:code`
  - `:collection`
- `:integer`
  - `:code`
  - `:number`
  - `:float`
  - `:complex`
  - `:ratio`
- `:integers`
  - `:code`
  - `:collection`
  - `:vector`
- `:ref`
  - `:code`
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
  - `:structured`
- `:vector`
  - `:code`
  - `:collection`


### New types

- `:matrix`
  - `:code`
  - `:collection`
  - `:vector`
  - `:structured`
- `:complex`
  - `:code`
  - `:number`
  - `:collection`
  - `:vector`
  - `:structured`
- `:rational`
  - `:code`
  - `:number`
  - `:collection`
  - `:vector`
  - `:structured`

### Some definitions for abstract types

- `:structured`
  
  a `hashmap` with specified keys; anything complex enough to be represented as a Clojure `record`
- `:collection`
  
  any `list`, `codeblock` `map`, `tagspace` or equivalent 
- `:number`
  
  You can probably figure this out.