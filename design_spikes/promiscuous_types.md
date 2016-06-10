# A structural, promiscuous type system in Push

## Goals

## Approach

- every Push item is of type `:code`
- every Push collection is of type `:collection`
- every Push number is of type `:number`
- some numbers are `:scalar`

### Reworking the existing types

- `:integer`
  - always
    - `:float`
    - `:ratio`
    - `:complex`
    - `:scalar`
  - sometimes
    - `:probability` (if 0 or 1)
- `:float`
  - always
    - `:complex`
    - `:ratio` (by reading decimal as fraction)
    - `:map` (`{:whole w :remainder r}`)
    - `:scalar`
   - sometimes
    - `:probability` (if between 0 and 1, inclusive)
    - `:integer` (if zero decimals)
- `ratio`
  - always
    - `:map` (`{:whole w :numerator n :denominator d}`)
    - `:complex`
    - `:scalar`
  - sometimes
    - `:float` (if not repeating?)
    - `:integer` (if `:numerator` is 0)
    - `:probability` (if within the right range)
- `:probability`
  - always
    - `:float`
    - `:ratio`
    - `:complex`
    - `:scalar`
  - sometimes
    - `:integer` (if 0 or 1)
- `:complex`
  - always
    - `:map`
  - sometimes
    - `:scalar` (if `:imaginary` is 0)
    - `:integer` 
    - `:float`
    - `:ratio`
    - `:probability`