package dev.yidafu.kotlin.lox

import kotlin.jvm.Throws

class ParseException(msg: String) : Exception(msg)

class LoxRuntimeException(var token: Token, msg: String = "") : RuntimeException(msg)

class LoxUndefinedException(name: String) : RuntimeException("Nil variable '$name'")

class LoxArgumentOverflowException : RuntimeException("function argument must less then 255")

class LoxCallableException : RuntimeException("Only function can be called")

class ReturnInterruptException(val value: AnyValue) : RuntimeException("")

@Throws
fun unreachable(): Nothing {
    throw ParseException("unreachable")
}
