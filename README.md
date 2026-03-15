# TypeResolver

This is a task for JetBrains Internship 2026.

# Task

You are building a light-weight static analysis tool. Your goal is to implement a `TypeResolver` that determines the type of a PHP variable based only on its `@var` documentation block using the following API:

- `PhpVariable`:
    - `getDocBlock()`: Returns the `PhpDocBlock` object, or `null`.
    - `getName()`: Returns the variable name (e.g., `"$user"`).


- `PhpDocBlock`:
    - `getTagsByName(String tagName)`: Returns a list of `DocTag` objects for a specific tag.


- `DocTag`:
    - `getValue()`: Returns the full text content (e.g., `"User $admin"`).


- `TypeFactory`:
    - `createType(String typeName)`: Converts a string to a `PhpType`.
    - `createUnionType(List<PhpType> types)`: Combines multiple `PhpType` objects into a `UnionType`.

<b>Task</b>: Implement the following function in Java or Kotlin. It must correctly identify which tag applies to the variable being inspected. Please do not use any external libraries or frameworks, except for testing purposes.
```

Kotlin

fun inferTypeFromDoc(variable: PhpVariable): PhpType {
// Implement logic here
}

Java

public function PhpType inferTypeFromDoc(PhpVariable variable) {
// Implement logic here
}
```
# Expected Behavior & Examples:

Standard Type: `/** @var User */` for `$user` → should return `User`.

Union Type: `/** @var string|int */` for `$id` → should return a `UnionType` of `string` and `int`.

Named Tag: `/** @var Logger $log */` for variable `$log` → should return `Logger`.

Name Mismatch: `/** @var Admin $adm */` for variable `$guest` → should return `mixed`. <br>(The tag is for a different variable, so it must be ignored).

Multiple Tags: If a `DocBlock` has `/** @var int $id */` and `/** @var string $name */`, and we are inspecting `$name`, the function should return `string`.

Fallback: If no `DocBlock` exists or no matching tag is found, return `mixed` using `TypeFactory.createType("mixed")`