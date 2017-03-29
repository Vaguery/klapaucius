---
title: "Klapaucius"
---
# The Klapaucius Push interpreter

This is the core documentation site for the Klapaucius interpreter, written in Clojure.

## The Push language

### Interpreter behavior

### Stacks and instructions

### Types

#### Numeric

- `:scalar`
- `:complex`
- `:interval`

#### Other basics

- `:boolean`
- `:char`

#### Programming

- `:code`
- `:exec`
- `:ref`
- `:snapshot`
- `:generator`
- `:quoted`

#### Collections

- `:string`
- `:tagspace`
- `:vector`
- `:vectorized` types
  - `:booleans`
  - `:chars`
  - `:complexes`
  - `:scalars`
  - `:intervals`
  - `:refs`
  - `:strings`
- `:set`

#### Introspection & Control

- `:behavior`
- `:log`
- `:error`
- `:return`
- `:print`

### Interpreter states

### Arguments and return values

### Special instructions

### Errors

### Program termination

## Extending the language

### Type or module?

### Designing new instruction
