package dev.yidafu.kotlin.lox.common

import dev.yidafu.kotlin.lox.interperter.AnyValue
import dev.yidafu.kotlin.lox.parser.Token
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

class LoxTopLevelSuperException : RuntimeException("can't use super from top-level")

class LoxSubClassSuerException : RuntimeException("can't use 'super' in class with no superclass")
class LoxInheritSelfException : RuntimeException("class can't inherit from itself")

class LoxSupperClassException : RuntimeException("Superclass must be Class")

class LoxDuplicateVariableException : RuntimeException("Can't declare variable with the some name twice")
@Throws
fun unreachable(): Nothing {
    throw ParseException("unreachable")
}
