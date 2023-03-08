package dev.yidafu.kotlin.lox

import kotlin.jvm.Throws

class ParseException(msg: String) : Exception(msg)

class LoxRuntimeException(var token: Token, msg: String = "") : RuntimeException(msg)

class LoxUndefinedException(name: String) : RuntimeException("Nil variable '$name'")

@Throws
fun unreachable(): Nothing {
    throw ParseException("unreachable")
}
