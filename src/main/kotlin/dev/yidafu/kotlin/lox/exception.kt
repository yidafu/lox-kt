package dev.yidafu.kotlin.lox

import kotlin.jvm.Throws

class ParseException(msg: String) : Exception(msg)

class LoxRuntimeException(var token: Token, msg: String = "") : RuntimeException(msg)

class LoxUndefinedException(name: String) : RuntimeException("Nil variable '$name'")

class LoxArgumentOverflowException : RuntimeException("function argument must less then 255")

class LoxCallableException : RuntimeException("Only function can be called")

class LoxReturnInterruptException(val value: AnyValue) : RuntimeException("")

class LoxVariableNotInitialException(name: String) : RuntimeException("variable not $name not initializer")

class LoxDeclareDuplicateException : RuntimeException("Already a variable with this name in this scope")

class LoxTopReturnException : RuntimeException("Can't return from top-level.")

class LoxNotObjectException : RuntimeException("Not object")

class LoxObjectNotHavePropertiesException : RuntimeException("object not have property")
class LoxTopLevelThisException : RuntimeException("can't use this from top-level")

@Throws
fun unreachable(): Nothing {
    throw ParseException("unreachable")
}
