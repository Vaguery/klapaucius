---
title: "Klapaucius"
---
# The Klapaucius Push interpreter

This is the core documentation site for the Klapaucius interpreter, written in Clojure.

## The Push language

### Interpreter behavior

### Stacks and instructions

### Basic Push syntax


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

- `:set`
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

### Type, module or both?

### Designing new instructions

### The Push DSL

### Stress-testing your libraries
