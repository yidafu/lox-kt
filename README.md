# Lox Kotlin

Lox language implement by Kotlin.

> see more: <http://www.craftinginterpreters.com/>

Implement AST Interpreter and ByteCode VM.

## Usage

See unit test cases.

## Example

Fibonacci sequence function.
```kotlin
fun fib(a) {
    if (a == 1) return 1;
    if (a == 2) return 2;
    return fib(a - 1) + fib(a - 2);
}
for (var i = 2; i < 10; i = i + 1) {
    print fib(i);
}
```
